package com.tahirikosan.pokemonnft.ui.fight

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.tahirikosan.pokemonnft.R
import com.tahirikosan.pokemonnft.base.BaseFragment
import com.tahirikosan.pokemonnft.data.response.fight.Room
import com.tahirikosan.pokemonnft.databinding.FragmentQueueBinding
import com.tahirikosan.pokemonnft.enum.PokemonStatEnum
import com.tahirikosan.pokemonnft.utils.FirebaseUtils
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.COLLECTION_QUEUE
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.COLLECTION_ROOMS
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.QUEUE_DOC_IC
import com.tahirikosan.pokemonnft.utils.FirebaseUtils.field_players

class QueueFragment : BaseFragment<FragmentQueueBinding>(FragmentQueueBinding::inflate) {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var queueRef: CollectionReference
    private lateinit var roomsRef: CollectionReference
    private lateinit var myPlayerId: String
    private var players: ArrayList<String> = arrayListOf()
    private lateinit var roomListener: ListenerRegistration

    // Fragment arguments.
    private val args: QueueFragmentArgs by navArgs()

    val selectedPokemon by lazy {
        args.selectedPokemon
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myPlayerId = FirebaseAuth.getInstance().currentUser!!.uid
        firestore = FirebaseFirestore.getInstance()

        // Write a message to the database
        // Write a message to the database
        queueRef = firestore.collection(COLLECTION_QUEUE)
        roomsRef = firestore.collection(COLLECTION_ROOMS)
        addPlayerToQueue(myPlayerId)

        listenChanges()

        binding.joinButton.setOnClickListener {

        }
    }

    override fun onStop() {
        super.onStop()
        removeUserFromQueue(playerId = myPlayerId)
    }

    override fun onDestroy() {
        super.onDestroy()
        removeUserFromQueue(playerId = myPlayerId)
    }

    private fun addPlayerToQueue(playerId: String) {
        val playerMap = hashMapOf(
            FirebaseUtils.playerId to playerId,
            FirebaseUtils.pokemonName to selectedPokemon.name,
            FirebaseUtils.hp to selectedPokemon.attributes!![PokemonStatEnum.HEALTH_STAT.index].value,
            FirebaseUtils.ap to selectedPokemon.attributes!![PokemonStatEnum.ATTACK_STAT.index].value,
            FirebaseUtils.dp to selectedPokemon.attributes!![PokemonStatEnum.DEFENCE_STAT.index].value,
            FirebaseUtils.sp to selectedPokemon.attributes!![PokemonStatEnum.SPEED_STAT.index].value,
            FirebaseUtils.imageUrl to selectedPokemon.image,
        )
        queueRef.document(QUEUE_DOC_IC)
            .update("${field_players}.${playerId}", playerMap)
    }

    private fun removeUserFromQueue(playerId: String) {
        queueRef.document(QUEUE_DOC_IC)
            .update("${field_players}.${playerId}", FieldValue.delete())
            .addOnSuccessListener {
                //binding.joinButton.visible(true)
            }
            .addOnFailureListener { e -> Log.w("TAG", "Error writing document", e) }
    }

    private fun routeUserToFightPage(roomId: String) {
        val enemyId = players.first {
            it != myPlayerId
        }
        val bundle: Bundle = bundleOf(
            "roomId" to roomId,
            "selectedPokemon" to selectedPokemon,
            "userId" to myPlayerId,
            "enemyId" to enemyId
        )
        findNavController().navigate(R.id.action_queueFragment_to_fightFragment, bundle)

    }


    private fun listenChanges() {
        // Listen game room and join it.
        roomListener = roomsRef.whereArrayContains(field_players, myPlayerId)
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
                            roomListener.remove()
                            routeUserToFightPage(roomId = room.roomId!!)
                        }
                        // DocumentChange.Type.MODIFIED -> Log.d(TAG, "Modified city: ${dc.document.data}")
                        //DocumentChange.Type.REMOVED -> Log.d(TAG, "Removed city: ${dc.document.data}")
                    }

                }
            }
    }

    private fun setPlayers(newplayers: ArrayList<String>) {
        if (players.size < 2) {
            players.clear()
            players.addAll(newplayers)
        }
    }
}