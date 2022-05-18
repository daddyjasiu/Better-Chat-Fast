package pl.edu.uj.ii.skwarczek.betterchatfast.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sendbird.calls.AuthenticateParams
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.SendBirdException
import com.sendbird.calls.User
import com.sendbird.calls.handler.AuthenticateHandler
import pl.edu.uj.ii.skwarczek.betterchatfast.util.Resource
import pl.edu.uj.ii.skwarczek.betterchatfast.util.SharedPreferencesManager

class AuthenticateViewModel : ViewModel() {
    private val _authenticateLiveData: MutableLiveData<Resource<User>> = MutableLiveData()
    val authenticationLiveData: LiveData<Resource<User>> = _authenticateLiveData

    fun authenticate(userId: String, accessToken: String? = null) {
        _authenticateLiveData.postValue(Resource.loading(null))
        if (userId.isEmpty()) {
            _authenticateLiveData.postValue(Resource.error("User ID is empty", null, null))
            return
        }

        val authenticateParams = AuthenticateParams(userId)
        if (accessToken != null) {
            authenticateParams.setAccessToken(accessToken)
        }

        SendBirdCall.authenticate(authenticateParams, object : AuthenticateHandler {
            override fun onResult(user: User?, e: SendBirdException?) {
                val resource = if (e == null) {
                    SharedPreferencesManager.userId = userId
                    SharedPreferencesManager.accessToken = accessToken
                    Resource.success(user)
                } else {
                    Resource.error(e.message, e.code, null)
                }
                _authenticateLiveData.postValue(resource)
            }
        })
    }
}
