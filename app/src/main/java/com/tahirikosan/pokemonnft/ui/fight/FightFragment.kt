package com.tahirikosan.pokemonnft.ui.fight

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.firestore.*
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.databinding.FragmentFightBinding
import com.tahirikosan.pokemonnft.data.response.fight.Room
import com.tahirikosan.pokemonnft.enum.PokemonStatEnum
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.COLLECTION_ROOMS
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.COLLECTION_USERS
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.ap
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.dp
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.field_coin
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.field_health
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.field_players
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.field_pvp
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.field_turn
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.hp
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.imageUrl
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.playersPokemons
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.pokemonName
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.sp
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.enable
import com.tahirikosan.pokemonnft.utils.Utils.onBackPressed
import com.tahirikosan.pokemonnft.utils.Utils.showToastShort
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FightFragment : BaseFragment<FragmentFightBinding>(FragmentFightBinding::inflate) {
    // Reduces pokemon damage.
    private val DAMAGE_REDUCER = 5

    private lateinit var firestore: FirebaseFirestore
    private lateinit var roomsRef: CollectionReference
    private lateinit var usersRef: CollectionReference
    private val args: FightFragmentArgs by navArgs()
    private lateinit var roomListener: ListenerRegistration

    private var isGameOver: Boolean = false
    private var isLeftTheGame: Boolean = false
    private var myMaxHpAlreadySet: Boolean = false
    private var enemyMaxHpAlreadySet: Boolean = false

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
        val attack = selectedPokemon.attributes!![PokemonStatEnum.ATTACK_STAT.index].value!! / DAMAGE_REDUCER
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
                .update("$field_health.${enemyId}", enemyHp).addOnSuccessListener {
                    // Set enemy hp view.
                    binding.progressEnemyHealth.progress = enemyHp
                    if (enemyHp == 0) {
                        isGameOver = true
                        youWon()
                    }
                    // After all, change attack turn.
                    changeTurn()
                }
        }
    }


    // Changes attacker turn.
    private fun changeTurn() {
        // Change turn
        roomsRef.document(roomId)
            .update(field_turn, enemyId)
            .addOnSuccessListener {
                binding.attackBtn.enable(false)
            }
            .addOnFailureListener {
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

                // Set enemy pokemon card view.
                setEnemyPokemonView(room!!)

                val myHp = room.healths!![userId]!!
                // Set enemy hp view.
                binding.progressMyHealth.progress = myHp
                setMyMaxHealthProgressOnce(myHp)
                // Check if your hp is zero. That means you lose.
                if (myHp == 0) {
                    isGameOver = true
                    youLose()
                }

                if (room.turn == userId) {
                    binding.attackBtn.enable(true)
                }

                // Check if enemy left the game.
                if (room.players!!.size == 1) {
                    // That means other user left the game. So you won
                    isGameOver = true
                    youWon()
                }
            } else {
                Log.d("TAG", "Current data: null")
            }
        }
    }

    private fun setMyMaxHealthProgressOnce(maxHp: Int) {
        if (!myMaxHpAlreadySet) {
            binding.progressMyHealth.max = maxHp
            binding.progressMyHealth.progress = maxHp
            myMaxHpAlreadySet = true
        }
    }

    private fun setEnemyMaxHealthProgressOnce(maxHp: Int) {
        if (!enemyMaxHpAlreadySet) {
            binding.progressEnemyHealth.max = maxHp
            binding.progressEnemyHealth.progress = maxHp
            enemyMaxHpAlreadySet = true
        }
    }


    private fun youWon() {
        usersRef.document(userId)
            .update(
                field_coin, FieldValue.increment(50), field_pvp, FieldValue.increment(10)
            ).addOnSuccessListener {
                deleteRoom()
                routeToGameResults(isWon = true)
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error updating document", e)
            }
    }

    private fun youLose() {
        usersRef.document(userId)
            .update(
                field_pvp, FieldValue.increment(-10)
            ).addOnSuccessListener {
                deleteRoom()
                routeToGameResults(isWon = false)
            }
    }

    // User closed game when game was continue. So punish him!
    private fun youLeftTheRoom() {
        Log.d("LEFT ROOM:", userId)
        roomsRef.document(roomId)
            .update(field_players, FieldValue.arrayRemove(userId))
        usersRef.document(userId)
            .update(
                field_pvp, FieldValue.increment(-50)
            )
    }

    private fun deleteRoom() {
        roomsRef.document(roomId).delete()
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