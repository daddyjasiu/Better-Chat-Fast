package pl.edu.uj.ii.skwarczek.betterchatfast.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_settings.*
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.databinding.FragmentSettingsBinding
import pl.edu.uj.ii.skwarczek.betterchatfast.signin.SignInActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.util.Status

class SettingsActivity: Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var db: FirebaseFirestore
    private val viewModel: SettingsViewModel = SettingsViewModel()
    lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View {
        auth= FirebaseAuth.getInstance()
        binding = DataBindingUtil.inflate(inflater, R.layout.activity_settings, container, false)
        setViewEventListener()
        observeViewModel()
        initView()
        binding.settingsTextViewUserId
        saveSettingsButton.setOnClickListener {

            val nickname = nicknameTextField.text.trim().toString()
            val firstName = firstNameTextField.text.trim().toString()
            val lastName = lastNameTextField.text.trim().toString()

            if(nickname.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()){
                val settingsHelper: ISettings = Settings()
                settingsHelper.setUserNickname(nickname)
                settingsHelper.setUserFirstName(firstName)
                settingsHelper.setUserLastName(lastName)
            }
            else{
                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }

        }
        return binding.root
    }
    private fun observeViewModel() {
        viewModel.deauthenticateLiveData.observe(requireActivity()) {
            when (it.status) {
                Status.SUCCESS -> {
                    auth.signOut()
                    val signOutIntent = Intent(activity, SignInActivity::class.java)
                    signOutIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(signOutIntent)
                }
            }
        }
    }
    private fun setViewEventListener() {
        binding.settingsImageViewArrowIcon.setOnClickListener{
            findNavController().navigateUp()
        }
    }

    private fun initView(){
        auth = Firebase.auth
        currentUser = auth.currentUser!!
        db = Firebase.firestore
    }
}