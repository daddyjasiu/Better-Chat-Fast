package pl.edu.uj.ii.skwarczek.betterchatfast.activities

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.adapters.OnboardingAdapter

class OnboardingActivity: AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var liquidViewPager: ViewPager
    private lateinit var onboardingAdapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        initView()

    }

    private fun initView(){
        auth = Firebase.auth
        currentUser = auth.currentUser!!

        onboardingAdapter = OnboardingAdapter(supportFragmentManager)
        liquidViewPager = findViewById(R.id.onboarding_liquid_view_pager)
        liquidViewPager.adapter = onboardingAdapter
    }
}
