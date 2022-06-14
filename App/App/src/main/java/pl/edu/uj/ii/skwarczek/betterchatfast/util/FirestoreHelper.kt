package pl.edu.uj.ii.skwarczek.betterchatfast.util

import android.content.ContentValues.TAG
import android.location.Address
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import pl.edu.uj.ii.skwarczek.betterchatfast.users.EMatchmakingStates
import pl.edu.uj.ii.skwarczek.betterchatfast.users.IUser

object FirestoreHelper {

    suspend fun getMatchmakingUsers(): ArrayList<DocumentSnapshot> = coroutineScope {
        val db = Firebase.firestore
        val result = arrayListOf<DocumentSnapshot>()

        db.collection("matchmaking")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    result.add(document)
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "User get failed!")
            }
            .await()

        return@coroutineScope result
    }

    fun addUserToMatchmakingList(user: IUser) {
        val db = Firebase.firestore

        db.collection("matchmaking")
            .document(user.userId)
            .set(user)
    }

    fun updateUserProfilePictureURL(url: String){
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser!!
        db.collection("users")
            .document(currentUser.uid)
            .update("profilePicture", url)
    }

    fun deleteCurrentUserFromMatchmaking(){
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser!!

        db.collection("matchmaking")
            .document(currentUser.uid)
            .delete()
    }

    fun addUserToFirestore(user: IUser) {
        val db = Firebase.firestore

        db.collection("users")
            .document(user.userId)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "User added with ID: ${user.userId}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding user", e)
            }
    }

    suspend fun getCurrentUserFromFirestore(): DocumentSnapshot = coroutineScope {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser!!
        var resultDocumentData: MutableMap<String, Any>? = mutableMapOf()

        val task = async {
            db.collection("users")
                .document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        resultDocumentData = document.data
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "User get failed!")
                }
                .await()
        }
        task.await()
    }

    fun updateCurrentUserLocation(location: Map<*, *>){
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser!!
        db.collection("users")
            .document(currentUser.uid)
            .update("location", location)
    }

    fun updateCurrentUserNickname(nickname: String) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser!!
        db.collection("users")
            .document(currentUser.uid)
            .update("nickname", nickname)
    }

    fun updateCurrentUserFirstName(firstName: String) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser!!
        db.collection("users")
            .document(currentUser.uid)
            .update("firstName", firstName)
    }

    fun updateCurrentUserLastName(lastName: String) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser!!
        db.collection("users")
            .document(currentUser.uid)
            .update("lastName", lastName)
    }

    fun updateCurrentUserIsPremium(isPremium: Boolean) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser!!
        db.collection("users")
            .document(currentUser.uid)
            .update("premium", isPremium)
    }

    fun updateCurrentUserMatchmakingState(matchmakingState: EMatchmakingStates){
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser!!
        db.collection("users")
            .document(currentUser.uid)
            .update("matchmakingState", matchmakingState)
    }

    fun deleteDocumentById(collectionId: String, documentId: String){
        val db = Firebase.firestore
        db.collection(collectionId).document(documentId)
            .delete()
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully deleted!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }
    }

    fun createDocumentById(collectionId: String, documentId: String, doc: Map<*,*>){
        val db = Firebase.firestore
        db.collection(collectionId).document(documentId)
            .set(doc)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully created!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error creating document", e) }
    }

    fun updateCurrentUserIsAfterOnboarding(isAfterOnboarding: Boolean) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser!!
        db.collection("users")
            .document(currentUser.uid)
            .update("afterOnboarding", isAfterOnboarding)
    }

   suspend fun getRoomById(roomId: String): DocumentSnapshot = coroutineScope{
        val db = Firebase.firestore
        val task = async {
            db.collection("rooms").document(roomId).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        document.data
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener {
                    Log.d(TAG, "User get failed!")
                }
                .await()
        }
        task.await()
    }

    fun updateCurrentUserRoomId(roomId: String){
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser!!
        db.collection("users")
            .document(currentUser.uid)
            .update("roomId", roomId)
    }
}