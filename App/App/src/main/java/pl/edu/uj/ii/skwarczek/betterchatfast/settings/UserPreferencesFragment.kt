package pl.edu.uj.ii.skwarczek.betterchatfast.settings

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
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
import pl.edu.uj.ii.skwarczek.betterchatfast.main.MainActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.util.*
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class UserPreferencesFragment : Fragment(), CoroutineScope {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var db: FirebaseFirestore
    private val viewModel: SettingsViewModel = SettingsViewModel()
    lateinit var binding: FragmentUserPreferencesBinding
    private var storageReference: StorageReference? = null
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private lateinit var imagePreview: ImageView
    private lateinit var btn_choose_image: Button
    private lateinit var btn_upload_image: Button

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        auth = FirebaseAuth.getInstance()
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_user_preferences, container, false)
        setUserInfo()

        initView()

        setViewEventListener()

        return binding.root
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {

            if (data == null || data.data == null) {
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, filePath)
                uploadImage()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun uploadImage() {
        if (filePath != null) {
            val ref = storageReference?.child("myImages/" + auth.currentUser!!.uid)
            ref?.putFile(filePath!!)
            val mail = auth.currentUser?.email

            //http post user
            val url = "https://api-$SENDBIRD_APP_ID.sendbird.com/v3/users/$mail"
            launch(Dispatchers.Main) {
                val iconPath = ref?.downloadUrl?.await().toString()

                FirestoreHelper.updateUserProfilePictureURL(iconPath)

                val postJSONObject = JSONObject(
                    """{
                                                "profile_url":"$iconPath"}"""
                )
                launch(Dispatchers.IO) {
                    RequestHandler.requestPUT(url, postJSONObject)
                    viewModel.deauthenticate()
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
            }

            Toast.makeText(context, "Profile photo uploaded!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "Please choose an image first", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setViewEventListener() {
        binding.userPreferencesImageViewLeftArrow.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.preferencesImageViewUserProfile.setOnClickListener {
            launchGallery()
        }

        binding.settingsSaveChangesButton.setOnClickListener {

            val nickname = binding.userPreferencesNicknameEditText.text.trim().toString()

            if (nickname.isNotEmpty()) {
                val settingsHelper: ISettings = Settings()
                settingsHelper.setUserNickname(nickname)

                view.let { activity?.hideKeyboard() }

                Toast.makeText(context, "User credentials updated!", Toast.LENGTH_SHORT).show()

                val mail = auth.currentUser?.email

                //http post user
                val url = "https://api-$SENDBIRD_APP_ID.sendbird.com/v3/users/$mail"
                launch(Dispatchers.Main) {

                    val postJSONObject = JSONObject(
                        """{
                                                "nickname":"$nickname"}"""
                    )
                    launch(Dispatchers.IO) {
                        RequestHandler.requestPUT(url, postJSONObject)
                        viewModel.deauthenticate()
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }

            } else {
                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun initView() {
        auth = Firebase.auth
        imagePreview = binding.preferencesImageViewUserProfile
        currentUser = auth.currentUser!!
        db = Firebase.firestore
        storageReference = FirebaseStorage.getInstance().reference
        firebaseStore = FirebaseStorage.getInstance()

    }
}