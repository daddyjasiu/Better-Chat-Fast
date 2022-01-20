package pl.edu.uj.ii.skwarczek.betterchatfast.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import kotlin.math.sign

class MainScreenActivity : AppCompatActivity() {

    private lateinit var signOutButton: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        initView()

        signOutButton.setOnClickListener {
            auth.signOut()
            val signOutIntent = Intent(this, SignInActivity::class.java)
            signOutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(signOutIntent)
        }
    }

    private fun initView(){
        auth = Firebase.auth
        signOutButton = findViewById(R.id.signout_button)
    }
}