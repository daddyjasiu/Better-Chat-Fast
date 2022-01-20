package pl.edu.uj.ii.skwarczek.productlist.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.activities.MainScreenActivity

class SignInTabFragment : Fragment(){

    private lateinit var auth: FirebaseAuth
    private lateinit var signInButton: Button
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var signInEmailField: EditText
    private lateinit var signInPasswordField: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_signin_tab, container, false)

        initView(view)

        signInButton.setOnClickListener {
            if(signInEmailField.text.toString().trim().isNotEmpty() && signInPasswordField.text.toString().trim().isNotEmpty()) {
                val email = signInEmailField.text.trim().toString()
                val password = signInPasswordField.text.trim().toString()
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful){
                            Toast.makeText(activity, "Logged in successfully!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(activity, MainScreenActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(activity, "Login failed, try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
            else{
                Toast.makeText(activity, "Please fill all required fields.", Toast.LENGTH_SHORT).show()
            }

        }

        forgotPasswordTextView.setOnClickListener {
            //TODO
        }

        return view
    }

    private fun initView(view: View){
        auth = Firebase.auth
        signInButton = view.findViewById(R.id.sign_in_button)
        forgotPasswordTextView = view.findViewById(R.id.forgot_password_text_view)
        signInEmailField = view.findViewById(R.id.sign_in_email)
        signInPasswordField = view.findViewById(R.id.sign_in_password)
    }
}