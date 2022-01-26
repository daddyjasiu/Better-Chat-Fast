package pl.edu.uj.ii.skwarczek.betterchatfast.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.R

class OnboardingActivity: AppCompatActivity() {

    private lateinit var finishOnboardingButton: Button

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        initView()

        finishOnboardingButton.setOnClickListener {
            val intent = Intent(this, MainScreenActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }

    private fun initView(){
        finishOnboardingButton = findViewById(R.id.finish_onboarding_button)
    }
}