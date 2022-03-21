package pl.edu.uj.ii.skwarczek.productlist.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.activities.MainScreenActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.activities.OnboardingActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.enums.UserTypes
import pl.edu.uj.ii.skwarczek.betterchatfast.utility.FirestoreHelper
import pl.edu.uj.ii.skwarczek.betterchatfast.utility.UserFactory

class SignUpTabFragment : Fragment(){

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var signUpButton: Button
    private lateinit var signUpEmailField: EditText
    private lateinit var signUpPasswordField: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_signup_tab, container, false)

        initView(view)

        signUpButton.setOnClickListener {
            if(signUpEmailField.text.toString().trim().isNotEmpty() && signUpPasswordField.text.toString().trim().isNotEmpty()){

                val email = signUpEmailField.text.trim().toString()
                val password = signUpPasswordField.text.trim().toString()

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activity, "Registered successfully!", Toast.LENGTH_SHORT).show()

                            //Adding user to the firestore only with email, adding additional info after onboarding
                            val newUser = UserFactory.createUser(
                                UserTypes.STANDARD,
                                userId = auth.currentUser?.uid!!,
                                "",
                                "",
                                "",
                                email,
                                "",
                                Location("")
                            )
                            FirestoreHelper.addUserToFirestore(newUser)

                            val intent = Intent(activity, OnboardingActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(activity, "Registration failed.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            else{
                Toast.makeText(activity, "Please fill all required fields.", Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }

    private fun initView(view: View){
        auth = Firebase.auth
        db = Firebase.firestore
        signUpButton = view.findViewById(R.id.sign_up_button)
        signUpEmailField = view.findViewById(R.id.sign_up_email_edit_text)
        signUpPasswordField = view.findViewById(R.id.sign_up_password_edit_text)
    }
}