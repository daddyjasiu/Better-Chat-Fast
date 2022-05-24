package pl.edu.uj.ii.skwarczek.betterchatfast.util

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


object MatchmakingHelper {

    suspend fun pairUsers() {

        val db = Firebase.firestore
        val numberOfParticipants = 2

        val allWaitingUsersQuerySnapshots = FirestoreHelper.getMatchmakingUsers()
        val currentUser = FirestoreHelper.getCurrentUserFromFirestore()
        val allWaitingUsersDocumentSnapshots: MutableList<Map<*, *>> = mutableListOf()

        for (userDocumentSnapshot in allWaitingUsersQuerySnapshots) {
            val map = userDocumentSnapshot.data?.get("data") as Map<*, *>
            allWaitingUsersDocumentSnapshots.add(map)
        }

        if(allWaitingUsersDocumentSnapshots.count() >= 2){
            val randomParticipants = allWaitingUsersDocumentSnapshots.asSequence().shuffled().take(numberOfParticipants).toMutableList()

            for(participant in randomParticipants){
                FirestoreHelper.deleteDocumentById("waiting", participant["userId"].toString())
                FirestoreHelper.createDocumentById("paired", participant["userId"].toString(), participant)
            }
        }
    }
}