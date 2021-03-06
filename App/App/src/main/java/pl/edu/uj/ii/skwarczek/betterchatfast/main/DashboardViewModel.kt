package pl.edu.uj.ii.skwarczek.betterchatfast.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sendbird.calls.*
import com.sendbird.calls.handler.CompletionHandler
import com.sendbird.calls.handler.RoomHandler
import pl.edu.uj.ii.skwarczek.betterchatfast.util.Resource
import pl.edu.uj.ii.skwarczek.betterchatfast.util.Status

class DashboardViewModel : ViewModel() {
    private val _createdRoomId: MutableLiveData<Resource<String>> = MutableLiveData()
    val createdRoomId: LiveData<Resource<String>> = _createdRoomId

    private val _fetchedRoomId: MutableLiveData<Resource<String>> = MutableLiveData()
    val fetchedRoomId: LiveData<Resource<String>> = _fetchedRoomId

    private val _enterResult: MutableLiveData<Resource<String>> = MutableLiveData()
    val enterResult: LiveData<Resource<String>> = _enterResult

    fun createAndEnterRoom() {
        if (_createdRoomId.value?.status == Status.LOADING) {
            return
        }

        _createdRoomId.postValue(Resource.loading(null))
        val params = RoomParams(RoomType.SMALL_ROOM_FOR_VIDEO)
        SendBirdCall.createRoom(params, object : RoomHandler {
            override fun onResult(room: Room?, e: SendBirdException?) {
                if (e != null) {
                    _createdRoomId.postValue(Resource.error(e.message, e.code, null))
                } else {
                    room?.enter(
                        EnterParams().setAudioEnabled(true).setVideoEnabled(true),
                        object : CompletionHandler {
                            override fun onResult(e: SendBirdException?) {
                                if (e != null) {
                                    _createdRoomId.postValue(
                                        Resource.error(
                                            e.message,
                                            e.code,
                                            null
                                        )
                                    )
                                } else {
                                    _createdRoomId.postValue(Resource.success(room.roomId))
                                }
                            }
                        })
                }
            }
        })
    }

    fun createRoom() {
        if (_createdRoomId.value?.status == Status.LOADING) {
            return
        }

        _createdRoomId.postValue(Resource.loading(null))
        val params = RoomParams(RoomType.SMALL_ROOM_FOR_VIDEO)
        SendBirdCall.createRoom(params, object : RoomHandler {
            override fun onResult(room: Room?, e: SendBirdException?) {
                if (e != null) {
                    _createdRoomId.postValue(Resource.error(e.message, e.code, null))
                } else {
                    _createdRoomId.postValue(Resource.success(room?.roomId))
                }
            }
        })
    }

    fun fetchRoomById(roomId: String) {
        if (roomId.isEmpty()) {
            return
        }

        if (_fetchedRoomId.value?.status == Status.LOADING) {
            return
        }

        _fetchedRoomId.postValue(Resource.loading(null))
        SendBirdCall.fetchRoomById(roomId, object : RoomHandler {
            override fun onResult(room: Room?, e: SendBirdException?) {
                if (e != null) {
                    _fetchedRoomId.postValue(Resource.error(e.message, e.code, null))
                } else {
                    _fetchedRoomId.postValue(Resource.success(room?.roomId))
                }
            }
        })
    }

    fun enter(roomId: String, isAudioEnabled: Boolean, isVideoEnabled: Boolean) {
        val room = SendBirdCall.getCachedRoomById(roomId) ?: return

        _enterResult.postValue(Resource.loading(null))
        val enterParams = EnterParams()
            .setAudioEnabled(isAudioEnabled)
            .setVideoEnabled(isVideoEnabled)

        room.enter(enterParams, object : CompletionHandler {
            override fun onResult(e: SendBirdException?) {
                if (e == null) {
                    _enterResult.postValue(Resource.success(roomId))
                } else {
                    _enterResult.postValue(Resource.error(e.message, e.code, null))
                }
            }
        })

    }
}
