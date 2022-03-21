package pl.edu.uj.ii.skwarczek.betterchatfast.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.interfaces.ISettings
import pl.edu.uj.ii.skwarczek.betterchatfast.models.Settings
import pl.edu.uj.ii.skwarczek.betterchatfast.utility.FirestoreHelper
import kotlin.coroutines.CoroutineContext

class MainScreenActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var db: FirebaseFirestore
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

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_details, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.settings_menu_item -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }

            R.id.about_us_menu_item -> {

            }

            R.id.sign_out_menu_item -> {
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
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initView(){
        auth = Firebase.auth
        currentUser = auth.currentUser!!
        db = Firebase.firestore
        testTextView = findViewById(R.id.test_main_screen_text_view)
    }
}