package pl.edu.uj.ii.skwarczek.betterchatfast.room

import android.content.BroadcastReceiver
import android.content.Intent
import android.os.Bundle
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.timer.TimerService
import pl.edu.uj.ii.skwarczek.betterchatfast.util.BaseActivity
import pl.edu.uj.ii.skwarczek.betterchatfast.util.EXTRA_IS_NEWLY_CREATED
import pl.edu.uj.ii.skwarczek.betterchatfast.util.EXTRA_ROOM_ID

class RoomActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)
    }

    fun getRoomId(): String {
        return intent.getStringExtra(EXTRA_ROOM_ID) ?: throw IllegalStateException()
    }

    fun isNewlyCreated(): Boolean {
        return intent.getBooleanExtra(EXTRA_IS_NEWLY_CREATED, false)
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.nav_host_room).let {
            it?.childFragmentManager?.fragments?.firstOrNull()
        }

        if (currentFragment is GroupCallFragment) {
            // ignore event
        } else {
            super.onBackPressed()
        }
    }
}