package pl.edu.uj.ii.skwarczek.betterchatfast.activities

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.models.StandardUser
import pl.edu.uj.ii.skwarczek.betterchatfast.utility.FirestoreHelper

class OnboardingActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var finishOnboardingButton: Button
    private lateinit var nicknameField: EditText
    private lateinit var firstNameField: EditText
    private lateinit var lastNameField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        initView()

        finishOnboardingButton.setOnClickListener {

            val nickname = nicknameField.text.trim().toString()
            val firstName = firstNameField.text.trim().toString()
            val lastName = lastNameField.text.trim().toString()

            if(nickname.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()){
                val user = StandardUser(currentUser.uid, nickname, firstName, lastName, currentUser.email!!, "profilepicwillbehere", Location("Warsaw"))
                FirestoreHelper.addUserToFirestore(user)

                val intent = Intent(this, MainScreenActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun initView(){
        auth = Firebase.auth
        currentUser = auth.currentUser!!
        finishOnboardingButton = findViewById(R.id.finish_onboarding_button)
        nicknameField = findViewById(R.id.onboarding_nickname_edit_text)
        firstNameField = findViewById(R.id.onboarding_first_name_edit_text)
        lastNameField = findViewById(R.id.onboarding_last_name_edit_text)
    }
}