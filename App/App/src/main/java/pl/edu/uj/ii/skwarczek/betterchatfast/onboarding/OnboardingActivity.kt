package pl.edu.uj.ii.skwarczek.betterchatfast.onboarding

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.onboarding.OnboardingAdapter

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
