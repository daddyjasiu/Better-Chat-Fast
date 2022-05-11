package pl.edu.uj.ii.skwarczek.betterchatfast.util

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = Color.parseColor("#2c2c2c")
    }
}