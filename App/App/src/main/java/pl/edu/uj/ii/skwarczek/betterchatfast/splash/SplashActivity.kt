package pl.edu.uj.ii.skwarczek.betterchatfast.splash

import android.content.Intent
import android.os.Bundle
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.signin.SendbirdSignInActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.util.BaseActivity
import java.util.*

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Timer().schedule(object : TimerTask() {
            override fun run() {
                val intent = Intent(this@SplashActivity, SendbirdSignInActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, SPLASH_INTERVAL)
    }

    companion object {
        const val SPLASH_INTERVAL = 2000L
    }
}
