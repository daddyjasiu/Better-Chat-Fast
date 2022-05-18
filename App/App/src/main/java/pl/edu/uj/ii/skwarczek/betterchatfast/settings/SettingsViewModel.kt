package pl.edu.uj.ii.skwarczek.betterchatfast.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sendbird.calls.SendBirdCall
import com.sendbird.calls.SendBirdException
import com.sendbird.calls.handler.CompletionHandler
import pl.edu.uj.ii.skwarczek.betterchatfast.util.Resource

class SettingsViewModel: ViewModel() {
    private val _deauthenticateLiveData: MutableLiveData<Resource<Unit>> = MutableLiveData()
    val deauthenticateLiveData: LiveData<Resource<Unit>> = _deauthenticateLiveData

    fun deauthenticate() {
        _deauthenticateLiveData.postValue(Resource.loading(null))
        SendBirdCall.deauthenticate(object : CompletionHandler {
            override fun onResult(e: SendBirdException?) {
                if (e != null) {
                    _deauthenticateLiveData.postValue(Resource.error(e.message, e.code, null))
                } else {
                    _deauthenticateLiveData.postValue(Resource.success(null))
                }
            }
        })
    }
}
