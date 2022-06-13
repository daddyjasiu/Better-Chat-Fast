package pl.edu.uj.ii.skwarczek.betterchatfast.queue

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sendbird.calls.SendBirdError
import kotlinx.android.synthetic.main.activity_queue.*
import kotlinx.coroutines.*
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.databinding.ActivityQueueBinding
import pl.edu.uj.ii.skwarczek.betterchatfast.main.DashboardViewModel
import pl.edu.uj.ii.skwarczek.betterchatfast.main.MainActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.preview.PreviewActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.room.RoomActivity
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
        FirestoreHelper.deleteCurrentUserFromMatchmaking()

        job.cancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loadingGifs = listOf(
            "https://cdn.dribbble.com/users/285475/screenshots/2928587/media/4e0475f22ed17d8d9d5036e1174c35ae.gif",
            "https://cdn.dribbble.com/users/285475/screenshots/2288892/media/1ea95fed5d5f092576447bf484182ee2.gif",
            "https://cdn.dribbble.com/users/285475/screenshots/2272756/media/f76c2cf6efa1ca7ed6f8db772fbe4a98.gif",
            "https://cdn.dribbble.com/users/285475/screenshots/1418440/media/a9e0fd9a6be22f3b7a0658b78e7d7eaa.gif",
            "https://cdn.dribbble.com/users/285475/screenshots/2640600/media/a1f63bc5114e8045aa8c30cc7e94cfdb.gif",
            "https://cdn.dribbble.com/users/285475/screenshots/2332606/media/0f91dfe9494ba29ad10abbebf69a93d4.gif",
            "https://cdn.dribbble.com/users/285475/screenshots/2083086/media/a683df6fd0b57db02968b6194c88d868.gif",
        )

        binding = DataBindingUtil.setContentView(this, R.layout.activity_queue)
        binding.queueCancelButton.setOnClickListener {
            FirestoreHelper.deleteCurrentUserFromMatchmaking()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        Glide.with(this)
            .load(loadingGifs.shuffled()[0])
            .into(imageview)

        launch(Dispatchers.Main) {

            var user = FirestoreHelper.getCurrentUserFromFirestore()

            while(user.get("matchmakingState").toString()!=EMatchmakingStates.IN_ROOM.toString()){
                user = FirestoreHelper.getCurrentUserFromFirestore()
                delay(1000)
            }
            launch(Dispatchers.Main) {
                var room = FirestoreHelper.getRoomById(user.get("roomId").toString())

                if (room.get("host") == user.get("userId")) {
                    viewModel.createAndEnterRoom()

                } else {
                    while (room.get("sendbirdId").toString() == "null") {
                        room = FirestoreHelper.getRoomById(user.get("roomId").toString())
                        delay(1000)
                    }
                    viewModel.fetchRoomById(room.get("sendbirdId").toString())
                }
            }
        }
        observeViewModel()
    }


    override fun onResume() {
        super.onResume()
        FirestoreHelper.updateCurrentUserMatchmakingState(EMatchmakingStates.IN_QUEUE)

    }
    private fun observeViewModel() {
        viewModel.createdRoomId.observe(this) { resource ->
            when (resource.status) {
                Status.LOADING -> {
                    // TODO : show loading view
                }
                Status.SUCCESS -> {
                    val db = Firebase.firestore
                    val roomId = resource.data
                    val auth = Firebase.auth
                    db.collection("rooms").document(
                        auth.currentUser!!.uid
                    ).update("sendbirdId", roomId)
                    resource.data?.let { goToRoomActivity(it) }
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
                Status.SUCCESS -> resource.data?.let { viewModel.enter(it,
                    isAudioEnabled = true,
                    isVideoEnabled = true
                ) }
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

        viewModel.enterResult.observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> resource.data?.let { goToRoomActivity(it) }

                Status.ERROR -> {
                    setResult(RESULT_ENTER_FAIL, Intent().apply {
                        putExtra(EXTRA_ENTER_ERROR_CODE, resource.errorCode)
                        putExtra(EXTRA_ENTER_ERROR_MESSAGE, resource.message)
                    })
                    finish()
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

    private fun goToRoomActivity(roomId: String) {
        val intent = Intent(this, RoomActivity::class.java).apply {
            putExtra(EXTRA_ROOM_ID, roomId)
            putExtra(EXTRA_IS_NEWLY_CREATED, true)
        }

        startActivity(intent)
    }

}