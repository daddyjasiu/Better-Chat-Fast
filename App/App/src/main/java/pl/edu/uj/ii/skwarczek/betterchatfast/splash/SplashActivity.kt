package pl.edu.uj.ii.skwarczek.betterchatfast.splash

import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.main.MainActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.signin.SignInActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.util.BaseActivity
import java.util.*

class SplashActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        auth = FirebaseAuth.getInstance()

        Timer().schedule(object : TimerTask() {
            override fun run() {
                if(auth.currentUser != null){
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                }
                else{
                    val intent = Intent(this@SplashActivity, SignInActivity::class.java)
                    startActivity(intent)
                }
                finish()
            }
        }, SPLASH_INTERVAL)
    }

    companion object {
        const val SPLASH_INTERVAL = 2000L
    }
}
