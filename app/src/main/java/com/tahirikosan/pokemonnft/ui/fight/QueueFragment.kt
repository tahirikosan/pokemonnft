package com.tahirikosan.pokemonnft.ui.fight

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.Person.fromBundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.tahirikosan.pokemonnft.R
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.data.response.fight.Room
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.Pokemon
import com.tahirikosan.pokemonnft.databinding.FragmentQueueBinding
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.visible

class QueueFragment : BaseFragment<FragmentQueueBinding>(FragmentQueueBinding::inflate) {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var queueRef: CollectionReference
    private lateinit var roomsRef: CollectionReference
    private lateinit var myUserId: String
    private var players: ArrayList<String> = arrayListOf()
    private lateinit var roomListener: ListenerRegistration

    // Fragment arguments.
    private val args: QueueFragmentArgs by navArgs()

    val selectedPokemon by lazy {
        args.selectedPokemon
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myUserId = FirebaseAuth.getInstance().currentUser!!.uid
        firestore = FirebaseFirestore.getInstance()

        // Write a message to the database
        // Write a message to the database
        queueRef = firestore.collection("queue")
        roomsRef = firestore.collection("rooms")
        addUserToQueue(userId = myUserId)

        listenChanges()

        binding.joinButton.setOnClickListener {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeUserFromQueue(userId = myUserId)
    }

    private fun addUserToQueue(userId: String) {
        queueRef.document("iji04WUR6e6Wq5ulOBFl")
            .update("players", FieldValue.arrayUnion(userId))
    }

    private fun removeUserFromQueue(userId: String) {
        queueRef.document("iji04WUR6e6Wq5ulOBFl")
            .update("players", FieldValue.arrayRemove(userId))
            .addOnSuccessListener {
                //binding.joinButton.visible(true)
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }

    private fun routeUserToFightPage(roomId: String) {
        // Stop listen to room changes.
        roomListener.remove()
        val enemyId = players.first {
            it != myUserId
        }
        val bundle: Bundle = bundleOf(
            "roomId" to roomId,
            "selectedPokemon" to selectedPokemon,
            "userId" to myUserId,
            "enemyId" to enemyId
        )
        findNavController().navigate(R.id.action_queueFragment_to_fightFragment, bundle)

    }


    private fun listenChanges() {
        // Listen for queue changes.
        /* queueRef.document("iji04WUR6e6Wq5ulOBFl").addSnapshotListener { snapshot, e ->
             if (e != null) {
                 Log.w("TAG", "Listen failed.", e)
                 return@addSnapshotListener
             }

             if (snapshot != null && snapshot.exists()) {
                 Log.d("TAG", "Current data: ${snapshot.data}")

                 // Check if queue is includes two userId.
                 setplayers(snapshot.data!!["players"] as ArrayList<String>)
                 if (players.size >= 2) {
                     Utils.showToastShort(requireContext(), "There are two user.")
                     // Then remove these players from queue and send them to match screen.
                     removeUserFromQueue(userId = myUserId)
                     binding.viewLoading.visible(false)
                 }
             } else {
                 Log.d("TAG", "Current data: null")
             }
         }*/

        // Listen game room and join it.
        roomListener = roomsRef.whereArrayContains("players", myUserId)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.w("TAG", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshots!!.documentChanges.isNullOrEmpty().not()) {
                    val dc = snapshots.documentChanges[0]
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> {
                            val room = dc.document.toObject(Room::class.java)
                            setPlayers(room.players!!)
                            routeUserToFightPage(roomId = room.roomId!!)
                        }
                        // DocumentChange.Type.MODIFIED -> Log.d(TAG, "Modified city: ${dc.document.data}")
                        //DocumentChange.Type.REMOVED -> Log.d(TAG, "Removed city: ${dc.document.data}")
                    }

                }
            }
        listenAndJoinRoom()
    }

    private fun listenAndJoinRoom() {

    }

    private fun setPlayers(newplayers: ArrayList<String>) {
        if (players.size < 2) {
            players.clear()
            players.addAll(newplayers)
        }
    }
}