package pl.edu.uj.ii.skwarczek.betterchatfast.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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

//        finishOnboardingButton.setOnClickListener {
//
//            val nickname = nicknameField.text.trim().toString()
//            val firstName = firstNameField.text.trim().toString()
//            val lastName = lastNameField.text.trim().toString()
//
//            if(nickname.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()){
//                val user = StandardUser(currentUser.uid, nickname, firstName, lastName, currentUser.email!!, "profilepicwillbehere", Location("Warsaw"))
//                FirestoreHelper.addUserToFirestore(user)
//
//                val intent = Intent(this, MainScreenActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                startActivity(intent)
//            }
//            else{
//                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
//            }
//
//        }
    }

    private fun initView(){
        auth = Firebase.auth
        currentUser = auth.currentUser!!

        onboardingAdapter = OnboardingAdapter(supportFragmentManager)
        liquidViewPager = findViewById(R.id.onboarding_liquid_view_pager)
        liquidViewPager.adapter = onboardingAdapter
    }
}