package pl.edu.uj.ii.skwarczek.betterchatfast.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.adapters.SignInAdapter

class SignInActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var signUpEmailField: EditText
    private lateinit var signUpPasswordField: EditText
    private lateinit var signInEmailField: EditText
    private lateinit var signInPasswordField: EditText
    private lateinit var googleActionButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        initView()

        googleActionButton.setOnClickListener{
            googleButtonClicked()
        }
    }

    private fun googleButtonClicked(){
        println("SWAGGERS")
    }

    private fun initView() {

        googleActionButton = findViewById(R.id.fab_google)

        tabLayout = findViewById(R.id.tab_layout)
        viewPager = findViewById(R.id.view_pager)

        val tabTitles = arrayOf("Sign in", "Sign up")

        tabLayout.addTab(tabLayout.newTab())
        tabLayout.addTab(tabLayout.newTab())
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        viewPager.adapter =
            SignInAdapter(supportFragmentManager, lifecycle, this, tabLayout.tabCount)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

}