package pl.edu.uj.ii.skwarczek.betterchatfast.util

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import pl.edu.uj.ii.skwarczek.betterchatfast.users.IUser

object FirestoreHelper {

    suspend fun pairUsers() {
        val db = Firebase.firestore

        val allWaitingUsersQuerySnapshots = getWaitingUsers()
        val currentUser = getCurrentUserFromFirestore()
        val allWaitingUsersDocumentSnapshots = mutableListOf<DocumentSnapshot>()

        for (userDocumentSnapshot in allWaitingUsersQuerySnapshots) {
            Log.d("SNAP", userDocumentSnapshot.toString())
            Log.d("USER", currentUser.toString())
        }

    }

    private suspend fun getWaitingUsers(): ArrayList<DocumentSnapshot> = coroutineScope{
        val db = Firebase.firestore
        val result = arrayListOf<DocumentSnapshot>()

        db.collection("waiting")
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

    fun addUserToPairedList(user: DocumentSnapshot) {
        val db = Firebase.firestore

        db.collection("paired")
            .document(user.get("userId").toString())
            .set(user)

    }

    fun addUserToWaitingList(user: DocumentSnapshot) {
        val db = Firebase.firestore

        db.collection("waiting")
            .document(user.get("userId").toString())
            .set(user)

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

    fun updateCurrentUserNicknameInFirebase(nickname: String) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser!!
        db.collection("users")
            .document(currentUser.uid)
            .update("nickname", nickname)
    }

    fun updateCurrentUserFirstNameInFirebase(firstName: String) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser!!
        db.collection("users")
            .document(currentUser.uid)
            .update("firstName", firstName)
    }

    fun updateCurrentUserLastNameInFirebase(lastName: String) {
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

    fun updateCurrentUserIsAfterOnboarding(isAfterOnboarding: Boolean) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser!!
        db.collection("users")
            .document(currentUser.uid)
            .update("afterOnboarding", isAfterOnboarding)
    }

}