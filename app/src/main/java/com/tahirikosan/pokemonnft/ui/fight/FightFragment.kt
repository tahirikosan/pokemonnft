package com.tahirikosan.pokemonnft.ui.fight

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.firestore.*
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.data.response.fight.PlayerPokemon
import com.tahirikosan.pokemonnft.databinding.FragmentFightBinding
import com.tahirikosan.pokemonnft.data.response.fight.Room
import com.tahirikosan.pokemonnft.enum.PokemonStatEnum
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.COLLECTION_ROOMS
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.COLLECTION_USERS
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.fieldCoin
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.fieldHealth
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.fieldPlayers
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.fieldPvp
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.fieldRound
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.fieldScore
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.field_turn
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.enable
import com.tahirikosan.pokemonnft.utils.Utils.onBackPressed
import com.tahirikosan.pokemonnft.utils.Utils.showToastShort
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FightFragment : BaseFragment<FragmentFightBinding>(FragmentFightBinding::inflate) {
    // Reduces pokemon damage.
    private val DAMAGE_REDUCER = 5
    private val TIMER_ATTACK_INTERVAL: Long = 10000

    private lateinit var firestore: FirebaseFirestore
    private lateinit var roomsRef: CollectionReference
    private lateinit var usersRef: CollectionReference
    private val args: FightFragmentArgs by navArgs()
    private lateinit var roomListener: ListenerRegistration

    // Sets one time in beginning of first round.
    private lateinit var initialTurn: String
    private var currentTurn: String = ""
    private var isInitialTurnSet: Boolean = false

    private var isGameOver: Boolean = false
    private var isLeftTheGame: Boolean = false
    private var myMaxHpAlreadySet: Boolean = false
    private var enemyMaxHpAlreadySet: Boolean = false

    private lateinit var playersPokemons: Map<String, PlayerPokemon>

    private val userId by lazy {
        args.userId
    }
    private val enemyId by lazy {
        args.enemyId
    }
    private val roomId by lazy {
        args.roomId
    }
    private val selectedPokemon by lazy {
        args.selectedPokemon
    }

    private var isTimerStarted = false
    val timer = object : CountDownTimer(TIMER_ATTACK_INTERVAL, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            binding.tvTimer.text = (millisUntilFinished / 1000).toString()
        }

        override fun onFinish() {
            // At end of timer change turn to enemy.
            if (currentTurn == userId) {
                changeTurn(enemyId)
            }
            resetTimer()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firestore = FirebaseFirestore.getInstance()
        roomsRef = firestore.collection(COLLECTION_ROOMS)
        usersRef = firestore.collection(COLLECTION_USERS)
        setMyPokemonView()
        listenChanges()
        Utils.showToastShort(requireContext(), selectedPokemon.name.toString() + "  " + roomId)


        binding.attackBtn.setOnClickListener {
            attack()
        }

        // Handle back button functionality.
        onBackPressed {

        }
    }

    override fun onStop() {
        super.onStop()
        // Check if suer left the game.
        if (!isGameOver) {
            isLeftTheGame = true
            youLeftTheRoom()
        }
    }

    override fun onResume() {
        super.onResume()
        // Check if suer left the game.
        if (isLeftTheGame) {
            findNavController().navigate(FightFragmentDirections.actionFightFragmentToGameMenuFragment())
        }
    }


    private fun setEnemyPokemonView(room: Room) {
        with(binding) {
            enemyPokemonView.tvPokemonName.text = room.playersPokemons?.get(enemyId)?.pokemonName
            enemyPokemonView.tvHp.text = room.playersPokemons?.get(enemyId)?.hp.toString()
            enemyPokemonView.tvAp.text = room.playersPokemons?.get(enemyId)?.ap.toString()
            enemyPokemonView.tvDp.text = room.playersPokemons?.get(enemyId)?.dp.toString()
            enemyPokemonView.tvSp.text = room.playersPokemons?.get(enemyId)?.sp.toString()
            // Set pokemon image.
            Glide.with(requireContext()).load(room.playersPokemons?.get(enemyId)?.imageUrl)
                .into(enemyPokemonView.ivPokemonImage)
        }
    }

    private fun setMyPokemonView() {
        with(binding) {
            myPokemonView.tvPokemonName.text =
                selectedPokemon.name
            myPokemonView.tvHp.text =
                selectedPokemon.attributes!![PokemonStatEnum.HEALTH_STAT.index].value.toString()
            myPokemonView.tvAp.text =
                selectedPokemon.attributes!![PokemonStatEnum.ATTACK_STAT.index].value.toString()
            myPokemonView.tvDp.text =
                selectedPokemon.attributes!![PokemonStatEnum.DEFENCE_STAT.index].value.toString()
            myPokemonView.tvSp.text =
                selectedPokemon.attributes!![PokemonStatEnum.SPEED_STAT.index].value.toString()
            // Set pokemon image.
            Glide.with(requireContext()).load(selectedPokemon.image)
                .into(myPokemonView.ivPokemonImage)
        }
    }

    private fun attack() {
        // Hit enemy and decrease it's health point.
        val criticalChance = selectedPokemon.attributes!![PokemonStatEnum.SPEED_STAT.index].value!!
        Log.d("CRIT Chance", criticalChance.toString())
        val attack =
            selectedPokemon.attributes!![PokemonStatEnum.ATTACK_STAT.index].value!! / DAMAGE_REDUCER
        val randomValue = (1..100).random()
        var finalDamage = attack
        // If hit crit.
        if (randomValue <= criticalChance) {
            finalDamage = attack * 2
            showToastShort(requireContext(), "CRIT:  $finalDamage")
        } else {
            showToastShort(requireContext(), "NORMAL: $finalDamage")
        }

        // Get enemy health info.
        roomsRef.document(roomId).get().addOnSuccessListener {
            val room = it.toObject(Room::class.java)
            var enemyHp = room!!.healths!![enemyId]!!
            setEnemyMaxHealthProgressOnce(enemyHp)
            // Decrease enemy hp with respect to user damage.
            enemyHp -= finalDamage
            if (enemyHp < 0) {
                enemyHp = 0
            }
            // Then update the enemyHp.
            roomsRef.document(roomId)
                .update("$fieldHealth.${enemyId}", enemyHp).addOnSuccessListener {
                    // Set enemy hp view.
                    setEnemyHealth(enemyHp)
                    if (enemyHp == 0) {
                        youWonTheRound()
                    }
                    // After all, change attack turn.
                    changeTurn(enemyId)
                }
        }
    }

    // Sets initial turn one time.
    private fun setInitialTurn(initialTurn: String) {
        if (!isInitialTurnSet) {
            isInitialTurnSet = true
            this.initialTurn = initialTurn
        }
    }

    // Changes attacker turn.
    private fun changeTurn(playerId: String) {
        // Change turn
        roomsRef.document(roomId)
            .update(field_turn, playerId)
            .addOnSuccessListener {
                binding.attackBtn.enable(false)
            }
            .addOnFailureListener {
            }
    }

    // Called when round changes. Decides next attacker.
    private fun checkAndChangeTurn() {
        // If my user has initial turn then give second round turn to enemy.(to be fair play)
        if (initialTurn == userId) {
            changeTurn(enemyId)
        } else {
            changeTurn(userId)
        }
    }

    // Listen room field changes like round, turn, health etc.
    private fun listenChanges() {
        roomListener = roomsRef.document(roomId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                Log.d("TAG", "Current data: ${snapshot.data}")
                val room = snapshot.toObject(Room::class.java)
                room?.let { room ->
                    playersPokemons = room.playersPokemons!!

                    setInitialTurn(room.turn!!)

                    // Check if enemy left the game.
                    checkIfEnemyLeftTheRoom(roomPLayersSize = room.players!!.size)
                    checkIfGameOver(room)

                    // Set Rounds view.
                    setRoundView(round = room.round!!)

                    // Set enemy pokemon card view.
                    setEnemyPokemonView(room)
                    val enemyHp = room.healths!![enemyId]!!
                    setEnemyHealth(enemyHp)

                    val myHp = room.healths!![userId]!!
                    // Set enemy hp view.
                    setMyMaxHealthProgressOnce(myHp)
                    setMyHealth(myHp)

                    // Set current turn
                    if (currentTurn != room.turn!!) {
                        currentTurn = room.turn!!
                        startTimer()
                    }

                    // Enable attackBtn.
                    if (room.turn == userId) {
                        binding.attackBtn.enable(true)
                    }
                }
            } else {
                Log.d("TAG", "Current data: null")
            }
        }
    }

    private fun setMyMaxHealthProgressOnce(maxHp: Int) {
        if (!myMaxHpAlreadySet) {
            binding.myHealthBar.max = maxHp
            binding.myHealthBar.progress = maxHp
            myMaxHpAlreadySet = true
        }
    }

    private fun setMyHealth(newHp: Int) {
        binding.myHealthBar.progress = newHp
        binding.tvMyHealth.text = newHp.toString()
    }

    private fun setEnemyMaxHealthProgressOnce(maxHp: Int) {
        if (!enemyMaxHpAlreadySet) {
            binding.enemyHealthBar.max = maxHp
            binding.enemyHealthBar.progress = maxHp
            enemyMaxHpAlreadySet = true
        }
    }

    private fun setEnemyHealth(newHp: Int) {
        binding.enemyHealthBar.progress = newHp
        binding.tvEnemyHealth.text = newHp.toString()
    }

    private fun setRoundView(round: Int) {
        binding.tvRound.text = "Round $round"
    }

    // My player won the current round, update his score in the room.
    private fun youWonTheRound() {
        // Then update the enemyHp.
        roomsRef.document(roomId)
            .update(
                "$fieldScore.${userId}",
                FieldValue.increment(1),
                "$fieldRound",
                FieldValue.increment(1)
            ).addOnSuccessListener {
                // Reset room.
                resetRoom()
                // After all, change attack turn.
                checkAndChangeTurn()
            }
    }

    // Checks users scores and defines the winner
    private fun checkIfGameOver(room: Room) {
        // Check enemy score.
        val myScore = room.scores!![userId]
        val enemyScore = room.scores!![enemyId]
        if (enemyScore == 2 && !isGameOver) {
            isGameOver = true
            youLose()
        }
        if (myScore == 2 && !isGameOver) {
            isGameOver = true
            youWon()
        }
    }

    private fun youWon() {
        deleteRoom()
        usersRef.document(userId)
            .update(
                fieldCoin, FieldValue.increment(50), fieldPvp, FieldValue.increment(10)
            ).addOnSuccessListener {
                routeToGameResults(isWon = true)
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error updating document", e)
            }
    }

    private fun youLose() {
        deleteRoom()
        usersRef.document(userId)
            .update(
                fieldPvp, FieldValue.increment(-10)
            ).addOnSuccessListener {
                routeToGameResults(isWon = false)
            }
    }

    // User closed game when game was continue. So punish him!
    private fun youLeftTheRoom() {
        Log.d("LEFT ROOM:", userId)
        roomsRef.document(roomId)
            .update(fieldPlayers, FieldValue.arrayRemove(userId))
        usersRef.document(userId)
            .update(
                fieldPvp, FieldValue.increment(-50)
            )
    }

    private fun checkIfEnemyLeftTheRoom(roomPLayersSize: Int) {
        if (roomPLayersSize == 1 && !isGameOver) {
            // That means other user left the game. So you won
            isGameOver = true
            youWon()
        }
    }

    private fun deleteRoom() {
        roomsRef.document(roomId).delete()
    }

    private fun resetRoom() {
        val enemyFullHp = playersPokemons[enemyId]!!.hp!!
        val myFullHp = playersPokemons[userId]!!.hp!!
        // First update user and enemy health on firestore.
        roomsRef.document(roomId)
            .update(
                "$fieldHealth.$enemyId",
                enemyFullHp,
                "$fieldHealth.$userId",
                myFullHp
            )
            .addOnSuccessListener {
                // Set enemy hp view.
                setEnemyHealth(enemyFullHp)
                setMyHealth(myFullHp)
            }
    }

    private fun startTimer() {
        if (!isTimerStarted) {
            timer.start()
        }
    }

    private fun stopTimer() {
        timer.cancel()
        isTimerStarted = false
    }

    private fun resetTimer() {
        stopTimer()
        startTimer()
    }

    private fun routeToGameResults(isWon: Boolean) {
        // Remove snapshot listener.
        roomListener.remove()
        findNavController().navigate(
            FightFragmentDirections.actionFightFragmentToFightResultFragment(
                isWon
            )
        )
    }
}