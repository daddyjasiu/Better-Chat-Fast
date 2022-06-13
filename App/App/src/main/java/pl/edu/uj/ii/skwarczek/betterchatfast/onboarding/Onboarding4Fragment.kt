package pl.edu.uj.ii.skwarczek.betterchatfast.onboarding

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.main.MainActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.signin.AuthenticateViewModel
import pl.edu.uj.ii.skwarczek.betterchatfast.util.RequestHandler
import pl.edu.uj.ii.skwarczek.betterchatfast.util.SENDBIRD_APP_ID
import pl.edu.uj.ii.skwarczek.betterchatfast.util.FirestoreHelper
import kotlin.coroutines.CoroutineContext

class Onboarding4Fragment : Fragment(), CoroutineScope {

    private lateinit var nicknameField: EditText
    private lateinit var firstNameField: EditText
    private lateinit var lastNameField: EditText
    private lateinit var iconUrlField: EditText
    private var storageReference: StorageReference? = null

    private lateinit var finishOnboardingButton: Button
    private lateinit var auth: FirebaseAuth
    private val viewModel: AuthenticateViewModel = AuthenticateViewModel()

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
    ): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding_4, container, false)

        initView(view)

        finishOnboardingButton.setOnClickListener {

            val nickname = nicknameField.text?.trim().toString()
            val firstName = firstNameField.text?.trim().toString()
            val lastName = lastNameField.text?.trim().toString()
            val iconUrl = iconUrlField.text?.trim().toString()

            if (nickname.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()) {
                FirestoreHelper.updateCurrentUserNickname(nickname)
                FirestoreHelper.updateCurrentUserFirstName(firstName)
                FirestoreHelper.updateCurrentUserLastName(lastName)
                FirestoreHelper.updateCurrentUserIsAfterOnboarding(true)


                val ref = storageReference?.child("myImages/" + auth.currentUser!!.uid)
                val mail = auth.currentUser?.email
                //http post user
                var iconPath: Uri? =null
                launch(Dispatchers.Main) {
                    iconPath = ref?.downloadUrl?.await()
                    Log.d("XD", iconPath.toString())


                    val url = "https://api-$SENDBIRD_APP_ID.sendbird.com/v3/users"
                    val postJSONObject = JSONObject(
                        """{"user_id":"$mail",
                         "nickname":"$nickname",
                 "profile_url":"$iconPath"}"""
                    )

                Thread(kotlinx.coroutines.Runnable {
                    RequestHandler.requestPOST(url, postJSONObject)
                }).start()
                }

                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return view
    }

    private fun initView(view: View) {
        auth = FirebaseAuth.getInstance()
        finishOnboardingButton = view.findViewById(R.id.onboarding_4_finish_button)
        nicknameField = view.findViewById(R.id.onboarding_4_nickname_edit_text)
        firstNameField = view.findViewById(R.id.onboarding_4_first_name_edit_text)
        lastNameField = view.findViewById(R.id.onboarding_4_last_name_edit_text)
        iconUrlField = view.findViewById(R.id.onboarding_4_icon_edit_text)
        storageReference = FirebaseStorage.getInstance().reference

    }

}