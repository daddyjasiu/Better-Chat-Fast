package pl.edu.uj.ii.skwarczek.betterchatfast.utility

import android.content.ContentValues.TAG
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import pl.edu.uj.ii.skwarczek.betterchatfast.interfaces.IUser

object FirestoreHelper {

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

//    suspend fun checkIfCurrentUserIsAfterOnboarding() : Boolean{
//
//    }

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