package pl.edu.uj.ii.skwarczek.betterchatfast.splash

import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.main.MainActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.onboarding.OnboardingActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.signin.SignInActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.util.BaseActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.util.FirestoreHelper
import java.util.*
import kotlin.coroutines.CoroutineContext

class SplashActivity : BaseActivity(), CoroutineScope {
    private lateinit var auth: FirebaseAuth

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        Timer().schedule(object : TimerTask() {
            override fun run() {
                if(auth.currentUser != null){
                    launch(Dispatchers.Main){
                        val user = FirestoreHelper.getCurrentUserFromFirestore()
                        //If the user is NOT new, but he hasn't finished onboarding, onboard him
                        if (user.get("afterOnboarding").toString() == "false") {
                            startActivity(Intent(baseContext, OnboardingActivity::class.java))
                            finish()
                        }
                        //If the user is NOT new and has finished onboarding, take him to main screen
                        else {
                            startActivity(Intent(baseContext, MainActivity::class.java))
                            finish()
                        }
                    }
                }
                else{
                    val intent = Intent(baseContext, SignInActivity::class.java)
                    startActivity(intent)
                }
            }
        }, SPLASH_INTERVAL)
    }

    companion object {
        const val SPLASH_INTERVAL = 2000L
    }
}
