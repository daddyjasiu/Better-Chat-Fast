package pl.edu.uj.ii.skwarczek.betterchatfast

import android.app.Application
import com.sendbird.calls.SendBirdCall
import pl.edu.uj.ii.skwarczek.betterchatfast.util.SharedPreferencesManager

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SendBirdCall.setLoggerLevel(SendBirdCall.LOGGER_INFO)
        SharedPreferencesManager.init(applicationContext)
    }
}
