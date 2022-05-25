package pl.edu.uj.ii.skwarczek.betterchatfast.queue

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.SendBirdError
import kotlinx.coroutines.*
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.databinding.ActivityQueueBinding
import pl.edu.uj.ii.skwarczek.betterchatfast.main.DashboardViewModel
import pl.edu.uj.ii.skwarczek.betterchatfast.main.MainActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.onboarding.OnboardingActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.preview.PreviewActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.signin.AuthenticateViewModel
import pl.edu.uj.ii.skwarczek.betterchatfast.users.EMatchmakingStates
import pl.edu.uj.ii.skwarczek.betterchatfast.util.*
import kotlin.coroutines.CoroutineContext

class QueueActivity: BaseActivity(), CoroutineScope {


    private val viewModel: DashboardViewModel = DashboardViewModel()

    private lateinit var binding: ActivityQueueBinding
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_queue)
        binding.queueCancelButton.setOnClickListener {

            FirestoreHelper.updateCurrentUserMatchmakingState(EMatchmakingStates.NOT_MATCHMAKING)
            FirestoreHelper.deleteCurrentUserFromMatchmaking()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        Log.d("matchmakin state","XD")

        launch(Dispatchers.Main) {
            Log.d("matchmakin state","XD2")

            var user = FirestoreHelper.getCurrentUserFromFirestore()
            Log.d("matchmakin state",user.get("matchmakingState").toString())

            while(user.get("matchmakingState").toString()!=EMatchmakingStates.IN_ROOM.toString()){
                user = FirestoreHelper.getCurrentUserFromFirestore()
                Log.d("matchmakin state",user.get("matchmakingState").toString())
                delay(1000)
            }
            launch(Dispatchers.Main) {
                var room = FirestoreHelper.getRoomById(user.get("roomId").toString())

                if (room.get("host") == user.get("userId")) {
                    val roomId = viewModel.createRoom()
                    val db = Firebase.firestore
                    Log.d("roomId", roomId)

                    db.collection("rooms").document(roomId).update("senbirdId", roomId)
                } else {
                    while (room.get("senbirdId").toString() == "") {
                        Log.d("sendbirdId", room.get("senbirdId").toString())

                        room = FirestoreHelper.getRoomById(user.get("roomId").toString())
                        delay(1000)
                    }
                    viewModel.fetchRoomById(room.get("senbirdId").toString())
                }
            }
        }
        observeViewModel()
    }
    private fun observeViewModel() {
        viewModel.createdRoomId.observe(this) { resource ->
            Log.d("DashboardFragment", "observe() resource: $resource")
            when (resource.status) {
                Status.LOADING -> {
                    // TODO : show loading view
                }
                Status.SUCCESS -> {

                    resource.data?.let { goToPreviewActivity(it) }
                }
                Status.ERROR -> {
                    val message = if (resource?.errorCode == SendBirdError.ERR_INVALID_PARAMS) {
                        getString(R.string.dashboard_invalid_room_params)
                    } else {
                        resource?.message
                    }
                    this.showAlertDialog(
                        getString(R.string.dashboard_can_not_create_room),
                        message ?: UNKNOWN_SENDBIRD_ERROR
                    )
                }
            }
        }

        viewModel.fetchedRoomId.observe(this) { resource ->
            Log.d("DashboardFragment", "observe() resource: $resource")
            when (resource.status) {
                Status.LOADING -> {
                    // TODO : show loading view
                }
                Status.SUCCESS -> resource.data?.let { goToPreviewActivity(it) }
                Status.ERROR -> {
                    this.showAlertDialog(
                        getString(R.string.dashboard_incorrect_room_id),
                        if (resource?.errorCode == 400200) {
                            getString(R.string.dashboard_incorrect_room_id_body)
                        } else {
                            resource?.message ?: UNKNOWN_SENDBIRD_ERROR
                        }
                    )
                }
            }
        }
    }
    private fun goToPreviewActivity(roomId: String) {
        val intent = Intent(this, PreviewActivity::class.java).apply {
            putExtra(EXTRA_ROOM_ID, roomId)
        }

        startActivityForResult(intent, REQUEST_CODE_PREVIEW)
    }

}