package com.tahirikosan.pokemonnft.ui.fight

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.firestore.*
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.databinding.FragmentFightBinding
import com.tahirikosan.pokemonnft.data.response.fight.Room
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.enable
import com.tahirikosan.pokemonnft.utils.Utils.onBackPressed
import dagger.hilt.android.AndroidEntryPoint
import kotlin.random.Random


@AndroidEntryPoint
class FightFragment : BaseFragment<FragmentFightBinding>(FragmentFightBinding::inflate) {


    private lateinit var firestore: FirebaseFirestore
    private lateinit var roomsRef: CollectionReference
    private lateinit var usersRef: CollectionReference
    private val args: FightFragmentArgs by navArgs()
    private lateinit var roomListener: ListenerRegistration

    private var isGameOver: Boolean = false
    private var isLeftTheGame: Boolean = false

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
        roomsRef = firestore.collection("rooms")
        usersRef = firestore.collection("users")
        setPlayerHp()
        listenChanges()
        Utils.showToastShort(requireContext(), selectedPokemon.name.toString() + "  " + roomId)


        binding.attackBtn.setOnClickListener {
            attack()
        }

        // Handle back button functionality.
        onBackPressed {
            //youLose()
            //roomsRef.document(roomId).delete()
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isGameOver) {
            isLeftTheGame = true
            youLeftTheRoom()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isLeftTheGame) {
            findNavController().navigate(FightFragmentDirections.actionFightFragmentToGameMenuFragment())
        }
    }

    private fun attack() {
        // Hit enemy and decrease it's health point.
        val speed = selectedPokemon.attributes!![3].value!!
        val attack = selectedPokemon.attributes!![2].value!!
        val randomValue = (1..speed).random()
        //val damage = (attack + (attack * randomValue) / 100).toDouble()
        val damage = (10 + (10 * randomValue) / 100).toDouble()
        // Get enemy health info.
        roomsRef.document(roomId).get().addOnSuccessListener {
            val room = it.toObject(Room::class.java)
            var enemyHp = room!!.health!![enemyId]!!
            // Decrease enemy hp with respect to user damage.
            enemyHp -= damage.toInt()
            if (enemyHp < 0) {
                enemyHp = 0
            }
            // Then update the enemyHp.
            roomsRef.document(roomId)
                .update("health.${enemyId}", enemyHp).addOnSuccessListener {
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
            .update("turn", enemyId)
            .addOnSuccessListener {
                binding.attackBtn.enable(false)
            }
            .addOnFailureListener {
            }
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

                // Check if your hp is zero. That means you lose.
                if (room!!.health!![userId]!! == 0) {
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
                    deleteRoom()
                    youWon()
                }
            } else {
                Log.d("TAG", "Current data: null")
            }
        }
    }

    private fun setPlayerHp() {
        roomsRef.document(roomId)
            .update(
                mapOf(
                    "health.${userId}" to selectedPokemon.attributes!![0].value
                )
            )
    }

    private fun youWon() {
        usersRef.document(userId)
            .update(
                "coin", FieldValue.increment(50), "pvp", FieldValue.increment(10)
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
                "pvp", FieldValue.increment(-10)
            ).addOnSuccessListener {
                deleteRoom()
                routeToGameResults(isWon = false)
            }
    }

    // User closed game when game was continue. So punish him!
    private fun youLeftTheRoom() {
        Log.d("LEFT ROOM:", userId)
        roomsRef.document(roomId)
            .update("players", FieldValue.arrayRemove(userId))
        usersRef.document(userId)
            .update(
                "pvp", FieldValue.increment(-50)
            )
    }

    private fun deleteRoom() {
        roomsRef.document(roomId).delete()
    }
}