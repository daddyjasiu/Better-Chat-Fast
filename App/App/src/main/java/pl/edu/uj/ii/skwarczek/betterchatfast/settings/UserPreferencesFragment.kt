package pl.edu.uj.ii.skwarczek.betterchatfast.settings

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sendbird.calls.SendBirdCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.databinding.FragmentUserPreferencesBinding
import pl.edu.uj.ii.skwarczek.betterchatfast.signin.AuthenticateViewModel
import pl.edu.uj.ii.skwarczek.betterchatfast.signin.SignInActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.util.*
import kotlin.coroutines.CoroutineContext

class UserPreferencesFragment: Fragment(),CoroutineScope {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var db: FirebaseFirestore
    private val viewModel: SettingsViewModel = SettingsViewModel()
    lateinit var binding: FragmentUserPreferencesBinding
    private var storageReference: StorageReference? = null

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?
    ): View {
        auth= FirebaseAuth.getInstance()
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_preferences, container, false)
        setUserInfo()
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

    private fun setUserInfo() {
        val user = SendBirdCall.currentUser

        val radius = activity?.dpToPixel(17) ?: 0
        Glide.with(this)
            .load(user?.profileUrl)
            .apply(
                RequestOptions()
                    .transform(RoundedCorners(radius))
                    .error(R.drawable.icon_avatar)
            )
            .into(binding.preferencesImageViewUserProfile)
    }
    private fun setViewEventListener() {
        binding.userPreferencesImageViewLeftArrow.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.preferencesImageViewUserProfile.setOnClickListener{
            Toast.makeText(context, "Img Button Clicked!", Toast.LENGTH_SHORT).show()
        }

        binding.settingsSaveChangesButton.setOnClickListener {

            val nickname = binding.userPreferencesNicknameEditText.text.trim().toString()

            if(nickname.isNotEmpty()){
                val settingsHelper: ISettings = Settings()
                settingsHelper.setUserNickname(nickname)

                view.let{activity?.hideKeyboard()}

                Toast.makeText(context, "User credentials updated!", Toast.LENGTH_SHORT).show()

                val mail = auth.currentUser?.email
                val ref = storageReference?.child("myImages/" + auth.currentUser!!.uid)

                //http post user
                val url = "https://api-$SENDBIRD_APP_ID.sendbird.com/v3/users/$mail"
                launch(Dispatchers.Main) {
                    val iconPath = ref?.downloadUrl?.await().toString()

                    val postJSONObject = JSONObject(
                        """{"user_id":"$mail",
                                                "nickname":"$nickname",
                                                "profile_url":"$iconPath"}"""
                    )
                    Thread(kotlinx.coroutines.Runnable {
                        RequestHandler.requestPUT(url, postJSONObject)
                    }).start()
                }

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
        storageReference = FirebaseStorage.getInstance().reference

    }
}