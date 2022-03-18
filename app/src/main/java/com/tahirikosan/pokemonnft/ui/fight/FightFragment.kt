package com.tahirikosan.pokemonnft.ui.fight

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.databinding.FragmentFightBinding
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tahirikosan.pokemonnft.data.response.fight.Room
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.onBackPressed
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FightFragment : BaseFragment<FragmentFightBinding>(FragmentFightBinding::inflate) {


    private lateinit var firestore: FirebaseFirestore
    private lateinit var roomsRef: CollectionReference
    private lateinit var usersRef: CollectionReference
    private val args: FightFragmentArgs by navArgs()

    private var isGameOver: Boolean = false

    private val userId by lazy {
        args.userId
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
        listenRoom()
        Utils.showToastShort(requireContext(), selectedPokemon.name.toString() + "  " + roomId)


        // Handle back button functionality.
        onBackPressed {
            //youLose()
            //roomsRef.document(roomId).delete()
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isGameOver) {
            youLeftTheRoom()
        }
    }

    private fun checkIfRoomExistThenDeleteIt() {
        /*  roomsRef.document(roomId)
              .get().addOnSuccessListener {
                  if (it.exists()) {
                      roomsRef.document(roomId).delete()
                  }
                  routeToGameMenu()
              }
              .addOnFailureListener {
                  routeToGameMenu()
              }*/
    }

    private fun routeToGameResults() {
        findNavController().navigate(FightFragmentDirections.actionFightFragmentToFightResultFragment())
    }

    // Listen room field changes like round, turn etc.
    private fun listenRoom() {
        roomsRef.document(roomId).addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                Log.d("TAG", "Current data: ${snapshot.data}")
                val room = snapshot.toObject(Room::class.java)

                if (room!!.players!!.size == 1) {
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

    private fun youWon() {
        usersRef.document(userId)
            .update(
                "coin", FieldValue.increment(50), "pvp", FieldValue.increment(10)
            ).addOnSuccessListener {
                routeToGameResults()
            }
            .addOnFailureListener { e ->
                Log.w("TAG", "Error updating document", e)
            }
    }

    private fun youLose() {
        usersRef.document(userId)
            .update(
                "pvp", FieldValue.increment(-10)
            )
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