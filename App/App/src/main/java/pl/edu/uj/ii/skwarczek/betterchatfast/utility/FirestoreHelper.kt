package pl.edu.uj.ii.skwarczek.betterchatfast.utility

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import pl.edu.uj.ii.skwarczek.betterchatfast.interfaces.ISettings
import pl.edu.uj.ii.skwarczek.betterchatfast.interfaces.IUser
import java.lang.Exception

object FirestoreHelper {

    fun addUserToFirestore(user: IUser){
        val db = Firebase.firestore

        db.collection("users")
            .document(user.userId)
            .set(user)
            .addOnSuccessListener {
                Log.d(ContentValues.TAG, "User added with ID: ${user.userId}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding user", e)
            }
    }

   suspend fun getCurrentUserFromFirestore(): MutableMap<String, Any>? {
        val db = Firebase.firestore
       val currentUser = Firebase.auth.currentUser!!
        var resultDocumentData: MutableMap<String, Any>? = mutableMapOf()

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
           return resultDocumentData
   }

//    suspend fun checkIfCurrentUserIsAfterOnboarding() : Boolean{
//
//    }

    fun updateCurrentUserNicknameInFirebase(nickname: String){
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser!!
        db.collection("users")
            .document(currentUser.uid)
            .update("nickname", nickname)
    }

    fun updateCurrentUserFirstNameInFirebase(firstName: String){
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser!!
        db.collection("users")
            .document(currentUser.uid)
            .update("firstName", firstName)
    }

    fun updateCurrentUserLastNameInFirebase(lastName: String){
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser!!
        db.collection("users")
            .document(currentUser.uid)
            .update("lastName", lastName)
    }

}