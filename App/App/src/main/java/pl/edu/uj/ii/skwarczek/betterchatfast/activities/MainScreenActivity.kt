package pl.edu.uj.ii.skwarczek.betterchatfast.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import kotlin.math.sign

class MainScreenActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var signOutButton: Button
    private lateinit var testTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_screen)

        initView()

        signOutButton.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Sign out")
                .setMessage("Do you really want to sign out?")
                .setNegativeButton("No") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .setPositiveButton("Yes") { _, _ ->
                    auth.signOut()
                    val signOutIntent = Intent(this, SignInActivity::class.java)
                    signOutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(signOutIntent)
                }
                .show()
        }

        testTextView.text = auth.currentUser!!.uid

    }

    private fun initView(){
        auth = Firebase.auth
        signOutButton = findViewById(R.id.signout_button)
        testTextView = findViewById(R.id.test_main_screen_text_view)
    }
}