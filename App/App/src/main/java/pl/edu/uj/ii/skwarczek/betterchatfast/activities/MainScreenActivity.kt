package pl.edu.uj.ii.skwarczek.betterchatfast.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.w3c.dom.Text
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.utility.FirestoreHelper
import kotlin.coroutines.CoroutineContext
import kotlin.math.sign

class MainScreenActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var db: FirebaseFirestore
    private lateinit var signOutButton: Button
    private lateinit var testTextView: TextView

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

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

        launch{
            testTextView.text = FirestoreHelper.getUserFromFirestore(currentUser)?.get("email").toString()
        }

    }

    private fun initView(){
        auth = Firebase.auth
        currentUser = auth.currentUser!!
        db = Firebase.firestore
        signOutButton = findViewById(R.id.signout_button)
        testTextView = findViewById(R.id.test_main_screen_text_view)
    }
}