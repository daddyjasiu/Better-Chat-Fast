package pl.edu.uj.ii.skwarczek.betterchatfast.activities

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.shadow.com.google.gson.Gson
import com.sendbird.calls.shadow.com.google.gson.JsonObject
import kotlinx.android.synthetic.main.fragment_signin_tab.*
import kotlinx.android.synthetic.main.fragment_signin_tab.view.*
import kotlinx.android.synthetic.main.fragment_signup_tab.view.*
import kotlinx.coroutines.*
import org.json.JSONObject
import pl.edu.uj.ii.skwarczek.betterchatfast.BuildConfig
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.adapters.SignInAdapter
import pl.edu.uj.ii.skwarczek.betterchatfast.enums.UserTypes
import pl.edu.uj.ii.skwarczek.betterchatfast.models.SendbirdUser
import pl.edu.uj.ii.skwarczek.betterchatfast.signin.SendbirdSignInActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.util.RequestHandler
import pl.edu.uj.ii.skwarczek.betterchatfast.util.SENDBIRD_APP_ID
import pl.edu.uj.ii.skwarczek.betterchatfast.util.SharedPreferencesManager
import pl.edu.uj.ii.skwarczek.betterchatfast.utility.FirestoreHelper
import pl.edu.uj.ii.skwarczek.betterchatfast.utility.UserFactory
import java.net.URL
import kotlin.coroutines.CoroutineContext


class SignInActivity : AppCompatActivity(), CoroutineScope {

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var googleActionButton: FloatingActionButton

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var oneTapClient: SignInClient

    private companion object {
        private const val TAG = "SignInActivity"
        private const val RC_GOOGLE_SIGN_IN = 2115
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        initView()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val client = GoogleSignIn.getClient(this, gso)

        //Trying to create new user in sendbird database
        val url = "https://api-$SENDBIRD_APP_ID.sendbird.com/v3/users"
        val newUser =  SendbirdUser("Jacob","Asty","https://sendbird.com/main/img/profiles/profile_05_512px.png")
        val postJSONObject = JSONObject("""{"user_id":"Jacob",
                                                "nickname":"Asty",
                                                "profile_url":"https://sendbird.com/main/img/profiles/profile_05_512px.png"}""")
        Thread(Runnable {
            RequestHandler.requestPOST(url, postJSONObject)
        }).start()


        googleActionButton.setOnClickListener {
            val signInIntent = client.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }
    }


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
//
    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser == null) {
            Log.w(TAG, "User is null, not going to navigate")
        } else {
            launch(Dispatchers.Main){
                val user = FirestoreHelper.getCurrentUserFromFirestore()
                //If the user is NOT new, but he hasn't finished onboarding, onboard him
                if (user.get("afterOnboarding").toString() == "false") {
                    startActivity(Intent(baseContext, OnboardingActivity::class.java))
                    finish()
                }
                //If the user is NOT new and has finished onboarding, take him to main screen
                else {

                    // Initialize SendBirdCall instance to use APIs in your app.
                    SendBirdCall.init(applicationContext, BuildConfig.SENDBIRD_APP_ID)
                    SendBirdCall.setLoggerLevel(SendBirdCall.LOGGER_INFO)
                    SharedPreferencesManager.init(applicationContext)

                    startActivity(Intent(baseContext, SendbirdSignInActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")

                    val isNewUser = task.result.additionalUserInfo?.isNewUser!!

                    //If the user is new, onboard him and create Firestore doc
                    if (isNewUser) {
                        val newUser = UserFactory.createUser(
                            UserTypes.STANDARD,
                            auth.currentUser?.uid!!,
                            "",
                            "",
                            "",
                            auth.currentUser?.email!!,
                            "",
                            Location("")
                        )
                        FirestoreHelper.addUserToFirestore(newUser)
                        startActivity(Intent(baseContext, OnboardingActivity::class.java))
                        finish()
                    } else {
                        val user = auth.currentUser
                        updateUI(user)
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Authentication failed!", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun initView() {

        auth = Firebase.auth
        db = Firebase.firestore
        googleActionButton = findViewById(R.id.fab_google)

        tabLayout = findViewById(R.id.sign_in_tab_layout)
        viewPager = findViewById(R.id.sign_in_view_pager)

        val tabTitles = arrayOf("Sign in", "Sign up")

        tabLayout.addTab(tabLayout.newTab())
        tabLayout.addTab(tabLayout.newTab())
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        viewPager.adapter =
            SignInAdapter(supportFragmentManager, lifecycle, tabLayout.tabCount)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

    }
}