package pl.edu.uj.ii.skwarczek.betterchatfast.activities

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.interfaces.ISettings
import pl.edu.uj.ii.skwarczek.betterchatfast.models.Settings

class SettingsActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var db: FirebaseFirestore
    private lateinit var nicknameTextField: EditText
    private lateinit var firstNameTextField: EditText
    private lateinit var lastNameTextField: EditText
    private lateinit var saveSettingsButton: Button

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initView()

        saveSettingsButton.setOnClickListener {

            val nickname = nicknameTextField.text.trim().toString()
            val firstName = firstNameTextField.text.trim().toString()
            val lastName = lastNameTextField.text.trim().toString()

            if(nickname.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()){
                val settingsHelper: ISettings = Settings()
                settingsHelper.setUserNickname(nickname)
                settingsHelper.setUserFirstName(firstName)
                settingsHelper.setUserLastName(lastName)
            }
            else{
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun initView(){
        auth = Firebase.auth
        currentUser = auth.currentUser!!
        db = Firebase.firestore
        nicknameTextField = findViewById(R.id.settings_nickname_edit_text)
        firstNameTextField = findViewById(R.id.settings_first_name_edit_text)
        lastNameTextField = findViewById(R.id.settings_last_name_edit_text)
        saveSettingsButton = findViewById(R.id.settings_save_changes_button)
    }
}