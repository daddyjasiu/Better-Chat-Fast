package pl.edu.uj.ii.skwarczek.betterchatfast.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.databinding.FragmentUserPreferencesBinding
import pl.edu.uj.ii.skwarczek.betterchatfast.signin.SignInActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.util.RequestHandler
import pl.edu.uj.ii.skwarczek.betterchatfast.util.SENDBIRD_APP_ID
import pl.edu.uj.ii.skwarczek.betterchatfast.util.Status
import pl.edu.uj.ii.skwarczek.betterchatfast.util.hideKeyboard

class UserPreferencesFragment: Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var db: FirebaseFirestore
    private val viewModel: SettingsViewModel = SettingsViewModel()
    lateinit var binding: FragmentUserPreferencesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View {
        auth= FirebaseAuth.getInstance()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_preferences, container, false)
        setViewEventListener()
        observeViewModel()
        initView()

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
        binding.userPreferencesImageViewLeftArrow.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.settingsSaveChangesButton.setOnClickListener {

            val nickname = binding.userPreferencesNicknameEditText.text.trim().toString()
            val firstName = binding.userPreferencesFirstNameEditText.text.trim().toString()
            val lastName = binding.userPreferencesLastNameEditText.text.trim().toString()

            if(nickname.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()){
                val settingsHelper: ISettings = Settings()
                settingsHelper.setUserNickname(nickname)
                settingsHelper.setUserFirstName(firstName)
                settingsHelper.setUserLastName(lastName)

                view.let{activity?.hideKeyboard()}

                Toast.makeText(context, "User credentials updated!", Toast.LENGTH_SHORT).show()

                val mail = auth.currentUser?.email
                //http post user
                val url = "https://api-$SENDBIRD_APP_ID.sendbird.com/v3/users/$mail"

                val postJSONObject = JSONObject(
                    """{"user_id":"$mail",
                                                "nickname":"$nickname",
                                                "profile_url":"https://sendbird.com/main/img/profiles/profile_05_512px.png"}"""
                )
                Thread(kotlinx.coroutines.Runnable {
                    RequestHandler.requestPUT(url, postJSONObject)
                }).start()

            }
            else{
                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun initView(){
        auth = Firebase.auth
        currentUser = auth.currentUser!!
        db = Firebase.firestore
    }
}