package pl.edu.uj.ii.skwarczek.betterchatfast.utility

import android.content.ContentValues
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pl.edu.uj.ii.skwarczek.betterchatfast.interfaces.IUser

object FirestoreHelper {

    fun addUserToFirestore(user: IUser){
        val db = Firebase.firestore

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(ContentValues.TAG, "User added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding user", e)
            }
    }
}