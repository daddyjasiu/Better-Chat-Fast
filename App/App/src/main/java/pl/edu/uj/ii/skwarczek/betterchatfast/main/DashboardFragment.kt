package pl.edu.uj.ii.skwarczek.betterchatfast.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.SendBirdError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.databinding.FragmentDashboardBinding
import pl.edu.uj.ii.skwarczek.betterchatfast.preview.PreviewActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.queue.QueueActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.room.RoomActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.signin.AuthenticateViewModel
import pl.edu.uj.ii.skwarczek.betterchatfast.users.EMatchmakingStates
import pl.edu.uj.ii.skwarczek.betterchatfast.users.EUserTypes
import pl.edu.uj.ii.skwarczek.betterchatfast.users.IUser
import pl.edu.uj.ii.skwarczek.betterchatfast.users.UserFactory
import pl.edu.uj.ii.skwarczek.betterchatfast.util.*
import kotlin.coroutines.CoroutineContext

class DashboardFragment : Fragment(), CoroutineScope {
    lateinit var binding: FragmentDashboardBinding
    private val viewModel: DashboardViewModel = DashboardViewModel()
    private lateinit var auth: FirebaseAuth
    private var chattingTimePicked = 30.0

    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false)
        setUserInfo()
        setViewEventListeners()
        observeViewModel()
        return binding.root
    }

    private fun setViewEventListeners() {

        auth = FirebaseAuth.getInstance()

//        binding.linearLayoutDashboard.setOnClickListener {
//            activity?.hideKeyboard()
//            binding.editTextRoomId.clearFocus()
//        }
//
//        binding.editTextRoomId.setOnFocusChangeListener { v, hasFocus ->
//            if (hasFocus) {
//                binding.textViewEnter.visibility = View.VISIBLE
//            } else {
//                if (binding.editTextRoomId.text?.toString().isNullOrEmpty()) {
//                    binding.textViewEnter.visibility = View.GONE
//                }
//            }
//        }
//
//        binding.dashboardCreateRoomButton.setOnClickListener {
//            viewModel.createAndEnterRoom()
//        }

        binding.dashboardSearchRoomButton.setOnClickListener {
            launch(Dispatchers.Main) {
                val currentUser = FirestoreHelper.getCurrentUserFromFirestore()

                val user: IUser = when (currentUser.get("premium")) {
                    true -> {
                        UserFactory.createUser(
                            EUserTypes.PREMIUM,
                            currentUser.id,
                            currentUser["nickname"].toString(),
                            currentUser["firstName"].toString(),
                            currentUser["lastName"].toString(),
                            currentUser["email"].toString(),
                            currentUser["profilePicture"].toString(),
                            EMatchmakingStates.IN_QUEUE,
                            "",
                            chattingTimePicked,
                        )
                    }
                    else -> {
                        UserFactory.createUser(
                            EUserTypes.STANDARD,
                            currentUser.id,
                            currentUser["nickname"].toString(),
                            currentUser["firstName"].toString(),
                            currentUser["lastName"].toString(),
                            currentUser["email"].toString(),
                            currentUser["profilePicture"].toString(),
                            EMatchmakingStates.IN_QUEUE,
                            "",
                            chattingTimePicked,
                        )
                    }
                }

                FirestoreHelper.addUserToMatchmakingList(user)

                val intent = Intent(context, QueueActivity::class.java).apply {
                    //putExtra(EXTRA_ROOM_ID, roomId)
                }

                startActivityForResult(intent, REQUEST_CODE_PREVIEW)
            }
        }

        val chattingTimes = arrayOf(30.0, 60.0, 120.0);
        val arrayAdapter = context?.let {
            ArrayAdapter<Double>(
                it,
                android.R.layout.simple_spinner_dropdown_item,
                chattingTimes
            )
        }
        binding.dashboardTimeSpinner.adapter = arrayAdapter
        binding.dashboardTimeSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    chattingTimePicked = chattingTimes[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    TODO("Not yet implemented")
                }

            }

//        binding.textViewEnter.setOnClickListener(this::onEnterButtonClicked)
    }

//    private fun onEnterButtonClicked(v: View) {
//        val roomId = binding.editTextRoomId.text?.toString() ?: return
//        viewModel.fetchRoomById(roomId)
//    }

    private fun setUserInfo() {
        val user = SendBirdCall.currentUser
        binding.textViewUserId.text = if (user?.userId.isNullOrEmpty()) {
            getString(R.string.no_nickname)
        } else {
            user?.userId
        }

        binding.textViewUserName.text = if (user?.nickname.isNullOrEmpty()) {
            getString(R.string.no_nickname)
        } else {
            user?.nickname
        }

        val radius = activity?.dpToPixel(17) ?: 0
        Glide.with(this)
            .load(user?.profileUrl)
            .apply(
                RequestOptions()
                    .transform(RoundedCorners(radius))
                    .error(R.drawable.icon_avatar)
            )
            .into(binding.imageViewUserProfile)
    }

    private fun observeViewModel() {
        viewModel.createdRoomId.observe(requireActivity()) { resource ->
            Log.d("DashboardFragment", "observe() resource: $resource")
            when (resource.status) {
                Status.LOADING -> {
                    // TODO : show loading view
                }
                Status.SUCCESS -> resource.data?.let { goToRoomActivity(it) }
                Status.ERROR -> {
                    val message = if (resource?.errorCode == SendBirdError.ERR_INVALID_PARAMS) {
                        getString(R.string.dashboard_invalid_room_params)
                    } else {
                        resource?.message
                    }
                    activity?.showAlertDialog(
                        getString(R.string.dashboard_can_not_create_room),
                        message ?: UNKNOWN_SENDBIRD_ERROR
                    )
                }
            }
        }

        viewModel.fetchedRoomId.observe(requireActivity()) { resource ->
            Log.d("DashboardFragment", "observe() resource: $resource")
            when (resource.status) {
                Status.LOADING -> {
                    // TODO : show loading view
                }
                Status.SUCCESS -> resource.data?.let { goToPreviewActivity(it) }
                Status.ERROR -> {
                    activity?.showAlertDialog(
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
        val intent = Intent(context, PreviewActivity::class.java).apply {
            putExtra(EXTRA_ROOM_ID, roomId)
        }

        startActivityForResult(intent, REQUEST_CODE_PREVIEW)
    }

    private fun goToRoomActivity(roomId: String) {
        val intent = Intent(context, RoomActivity::class.java).apply {
            putExtra(EXTRA_ROOM_ID, roomId)
            putExtra(EXTRA_IS_NEWLY_CREATED, true)
        }

        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_PREVIEW && resultCode == RESULT_ENTER_FAIL) {
            val errorCode = data?.getIntExtra(EXTRA_ENTER_ERROR_CODE, -1)
            val errorMessage =
                if (errorCode == SendBirdError.ERR_PARTICIPANTS_LIMIT_EXCEEDED_IN_ROOM) {
                    getString(R.string.dashboard_can_not_enter_room_max_participants_count_exceeded)
                } else {
                    data?.getStringExtra(EXTRA_ENTER_ERROR_MESSAGE)
                } ?: UNKNOWN_SENDBIRD_ERROR

            activity?.showAlertDialog(
                getString(R.string.dashboard_can_not_enter_room),
                errorMessage
            )
        }
    }
}
