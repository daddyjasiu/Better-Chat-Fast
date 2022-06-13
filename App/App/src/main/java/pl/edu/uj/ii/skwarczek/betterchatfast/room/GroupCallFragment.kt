package pl.edu.uj.ii.skwarczek.betterchatfast.room

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sendbird.calls.AudioDevice
import com.sendbird.calls.SendBirdCall
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.databinding.FragmentGroupCallBinding
import pl.edu.uj.ii.skwarczek.betterchatfast.main.MainActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.timer.TimerService
import pl.edu.uj.ii.skwarczek.betterchatfast.util.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext
import kotlin.math.roundToInt

class GroupCallFragment : Fragment(), CoroutineScope {
    lateinit var binding: FragmentGroupCallBinding
    lateinit var viewModel: GroupCallViewModel
    private var isNewlyCreatedRoomInfoShown = false

    private var auth = Firebase.auth

    private var timerStarted = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_call, container, false)
        val roomId = (activity as? RoomActivity)?.let {
//            if (it.isNewlyCreated() && !isNewlyCreatedRoomInfoShown) {
//                showNewlyCreatedRoomInfo(it.getRoomId())
//                isNewlyCreatedRoomInfoShown = true
//            }

            viewModel = GroupCallViewModel(it.getRoomId())
            it.getRoomId()
        } ?: throw IllegalStateException()

        initView(roomId)

        serviceIntent = Intent(context, TimerService::class.java)
        activity?.registerReceiver(updateTime, IntentFilter(TimerService.TIMER_UPDATED))


        val handler = Handler()
        val runnable = Runnable {
            viewModel.exit()
        }

        launch(Dispatchers.Main) {
            val user = FirestoreHelper.getCurrentUserFromFirestore()
            when (user["roomId"].toString() != user["userId"].toString()) {
                true -> {
                    startTimer()
                    handler.postDelayed(runnable, 30_000)
                    Log.d("NIEPRAWDA_roomId", user["roomId"].toString())
                    Log.d("NIEPRAWDA_userId", user["userId"].toString())
                }
            }
        }

        return binding.root
    }

    private fun initView(roomId: String) {
        val room = SendBirdCall.getCachedRoomById(roomId)

        // views
//        binding.groupCallTextViewRoomId.text = roomId
        setAudioEnabledImage(room?.localParticipant?.isAudioEnabled ?: false)
        setVideoEnabledImage(room?.localParticipant?.isVideoEnabled ?: false)

        // RecyclerView
        initRecyclerView()

        // ViewModel
        observeViewModel()

        // events
        binding.groupCallImageViewSpeaker.setOnClickListener {
            showSelectingAudioDeviceDialog()
        }

        binding.groupCallImageViewCameraFlip.setOnClickListener {
            viewModel.switchCamera()
        }

        binding.groupCallImageViewVideoOnOff.setOnClickListener {
            val isVideoEnabled = room?.localParticipant?.isVideoEnabled ?: return@setOnClickListener
            if (isVideoEnabled) {
                viewModel.stopLocalVideo()
            } else {
                viewModel.startLocalVideo()
            }
        }

        binding.groupCallImageViewAudioOnOff.setOnClickListener {
            val isAudioEnabled = room?.localParticipant?.isAudioEnabled ?: return@setOnClickListener
            if (isAudioEnabled) {
                viewModel.muteMicrophone()
            } else {
                viewModel.unmuteMicrophone()
            }
        }

        binding.groupCallImageViewExit.setOnClickListener {
            viewModel.exit()
        }

        binding.groupCallLinearLayoutParticipants.setOnClickListener {
            val action = GroupCallFragmentDirections
                .actionGroupCallFragmentToParticipantListFragment(roomId)
            findNavController().navigate(action)
        }

//        binding.groupCallLinearLayoutRoomIdInfo.setOnClickListener {
//            val action = GroupCallFragmentDirections
//                .actionGroupCallFragmentToRoomInfoFragment(roomId)
//            findNavController().navigate(action)
//        }
    }

    private fun resetTimer() {
        stopTimer()
        time = 0.0
        binding.groupCallTimer.text = getTimeStringFromDouble(time)
    }

    private fun startStopTimer() {
        if (timerStarted)
            stopTimer()
        else
            startTimer()
    }

    private fun startTimer() {
        serviceIntent.putExtra(TimerService.TIME_EXTRA, time)
        activity?.startService(serviceIntent)
        timerStarted = true
    }

    private fun stopTimer() {
        activity?.stopService(serviceIntent)
        timerStarted = false
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(TimerService.TIME_EXTRA, 0.0)
            if (time >= 30.0) {
                binding.groupCallTimer.text = getTimeStringFromDouble(30.0)
                stopTimer()
                resetTimer()
            } else {
                binding.groupCallTimer.text = getTimeStringFromDouble(time)
            }
        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hour: Int, min: Int, sec: Int): String =
        String.format("%02d:%02d:%02d", hour, min, sec)


    private fun getRoomTimeCreatedAt(roomId: String): String {
        val room = SendBirdCall.getCachedRoomById(roomId)

        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        val instant = room?.createdAt?.let {
            Instant.ofEpochMilli(
                it
            )
        }
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return formatter.format(date)
    }

    private fun initRecyclerView() {
        val recyclerView = binding.groupCallRecyclerViewParticipants
        val adapter = ParticipantAdapter(viewModel.participants.value ?: listOf())
        val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
            justifyContent = JustifyContent.CENTER
        }
        recyclerView.layoutManager = flexboxLayoutManager
        recyclerView.adapter = adapter

        viewModel.participants.observe(viewLifecycleOwner) { participants ->
            adapter.participants = participants
        }
    }

    private fun observeViewModel() {
        viewModel.localParticipant.observe(viewLifecycleOwner) {
            if (it != null) {
                setAudioEnabledImage(it.isAudioEnabled)
                setVideoEnabledImage(it.isVideoEnabled)
            }
        }

        viewModel.isExited.observe(viewLifecycleOwner) {
            if (it.status == Status.SUCCESS) {
                activity?.finish()
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                val message = it.message ?: return@observe
                activity?.showToast(message)
            }
        }

        viewModel.currentAudioDevice.observe(viewLifecycleOwner) {
            if (it != null) {
                val resource = when (it) {
                    AudioDevice.EARPIECE -> R.drawable.icon_bluetooth_white
                    AudioDevice.SPEAKERPHONE -> R.drawable.icon_speaker
                    AudioDevice.WIRED_HEADSET -> R.drawable.icon_headset_white
                    AudioDevice.BLUETOOTH -> R.drawable.icon_bluetooth_white
                }

                binding.groupCallImageViewSpeaker.setImageResource(resource)
            }
        }

        viewModel.participantsTimer.observe(viewLifecycleOwner) {
            startTimer()
        }

    }

    private fun setAudioEnabledImage(isEnabled: Boolean) {
        binding.groupCallImageViewAudioOnOff.setImageResource(
            if (isEnabled) {
                R.drawable.btn_audio_off
            } else {
                R.drawable.btn_audio_off_selected
            }
        )
    }

    private fun setVideoEnabledImage(isEnabled: Boolean) {
        binding.groupCallImageViewVideoOnOff.setImageResource(
            if (isEnabled) {
                R.drawable.btn_video_off
            } else {
                R.drawable.btn_video_off_selected
            }
        )
    }

    private fun showSelectingAudioDeviceDialog() {
        val audioDevices = viewModel.getAvailableAudioDevices().toList()
        val currentAudioDevice = viewModel.currentAudioDevice.value
        val currentAudioDeviceIndex = audioDevices.indexOfFirst { it == currentAudioDevice }.let {
            if (it == -1) {
                return@let 0
            }

            it
        }

        var selectedIndex = 0
        AlertDialog.Builder(context)
            .setTitle(R.string.group_call_select_audio_device_dialog_title)
            .setSingleChoiceItems(
                audioDevices.map { it.toReadableString() }.toTypedArray(),
                currentAudioDeviceIndex
            ) { _, which ->
                selectedIndex = which
            }
            .setPositiveButton(R.string.ok) { _, _ ->
                if (currentAudioDeviceIndex != selectedIndex) {
                    viewModel.selectAudioDevice(audioDevices[selectedIndex])
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
            .show()
    }

    private fun showNewlyCreatedRoomInfo(roomId: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_room_creation, null)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.dialog_text_view_room_id).text = roomId
        dialogView.findViewById<TextView>(R.id.dialog_text_view_ok).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<ImageView>(R.id.dialog_image_view_copy).setOnClickListener {
            activity?.copyText(roomId)
            activity?.showToast(getString(R.string.group_call_copied))
        }

        dialog.show()
    }
}
