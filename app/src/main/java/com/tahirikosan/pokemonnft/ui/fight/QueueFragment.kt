package com.tahirikosan.pokemonnft.ui.fight

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.Person.fromBundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.tahirikosan.pokemonnft.R
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.data.response.ownerpokemons.Pokemon
import com.tahirikosan.pokemonnft.databinding.FragmentQueueBinding
import com.tahirikosan.pokemonnft.utils.Utils
import com.tahirikosan.pokemonnft.utils.Utils.visible

class QueueFragment : BaseFragment<FragmentQueueBinding>(FragmentQueueBinding::inflate) {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var queueRef: CollectionReference
    private lateinit var roomsRef: CollectionReference
    private lateinit var myUserId: String
    private var users: ArrayList<String> = arrayListOf()

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
            searchForRoomAndJoinIfExist()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeUserFromQueue(userId = myUserId)
    }

    private fun addUserToQueue(userId: String) {
        queueRef.document("iji04WUR6e6Wq5ulOBFl")
            .update("users", FieldValue.arrayUnion(userId))
    }

    private fun removeUserFromQueue(userId: String) {
        queueRef.document("iji04WUR6e6Wq5ulOBFl")
            .update("users", FieldValue.arrayRemove(userId))
            .addOnSuccessListener {
                binding.joinButton.visible(true)
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }

    private fun searchForRoomAndJoinIfExist() {
        firestore.collection("rooms")
            .whereArrayContains("users", myUserId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    createARoom()
                } else {
                    routeUserToFightPage(snapshot.documents[0].id)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("TAG", "Error getting documents: ", exception)
                createARoom()
            }
    }

    private fun createARoom() {
        val docRef = roomsRef.document()
        val room = hashMapOf(
            "round" to 1,
            "turn" to myUserId,
            "users" to users,
            "roomId" to docRef.id
        )
        docRef.set(
            room
        ).addOnSuccessListener {
            routeUserToFightPage(docRef.id)
        }.addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }

    private fun routeUserToFightPage(roomId: String) {
        val bundle: Bundle = bundleOf(
            "roomId" to roomId,
            "selectedPokemon" to selectedPokemon,
            "userId" to myUserId
        )
        findNavController().navigate(R.id.action_queueFragment_to_fightFragment, bundle)
    }


    private fun listenChanges() {
        // Listen for queue changes.
        queueRef.document("iji04WUR6e6Wq5ulOBFl").addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("TAG", "Current data: ${snapshot.data}")

                // Check if queue is includes two userId.
                setUsers(snapshot.data!!["users"] as ArrayList<String>)
                if (users.size >= 2) {
                    Utils.showToastShort(requireContext(), "There are two user.")
                    // Then remove these users from queue and send them to match screen.
                    removeUserFromQueue(userId = myUserId)
                    binding.viewLoading.visible(false)
                }
            } else {
                Log.d("TAG", "Current data: null")
            }
        }
    }

    private fun setUsers(newUsers: ArrayList<String>) {
        if (users.size < 2) {
            users.clear()
            users.addAll(newUsers)
        }
    }
}