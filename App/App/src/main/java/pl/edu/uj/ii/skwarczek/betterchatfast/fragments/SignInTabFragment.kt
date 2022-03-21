package pl.edu.uj.ii.skwarczek.betterchatfast.fragments

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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.activities.MainScreenActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.activities.OnboardingActivity
import kotlin.coroutines.CoroutineContext

class SignInTabFragment : Fragment(), CoroutineScope{

    private lateinit var auth: FirebaseAuth
    private lateinit var signInButton: Button
    private lateinit var forgotPasswordTextView: TextView
    private lateinit var signInEmailField: EditText
    private lateinit var signInPasswordField: EditText

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

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

                            launch(Dispatchers.Main){
                                val user = pl.edu.uj.ii.skwarczek.betterchatfast.utility.FirestoreHelper.getCurrentUserFromFirestore()
                                //If the user is NOT new, but he hasn't finished onboarding, onboard him
                                if (user.get("afterOnboarding").toString() == "false") {
                                    val intent = Intent(activity, OnboardingActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                }
                                //If the user is NOT new and has finished onboarding, take him to main screen
                                else {
                                    val intent = Intent(activity, MainScreenActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    startActivity(intent)
                                }
                            }
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
        signInEmailField = view.findViewById(R.id.sign_in_email_edit_text)
        signInPasswordField = view.findViewById(R.id.sign_in_password_edit_text)
    }
}