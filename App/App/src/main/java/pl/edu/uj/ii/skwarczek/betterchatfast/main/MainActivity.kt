package pl.edu.uj.ii.skwarczek.betterchatfast.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.sendbird.calls.SendBirdCall
import pl.edu.uj.ii.skwarczek.betterchatfast.R
import pl.edu.uj.ii.skwarczek.betterchatfast.databinding.ActivityMainBinding
import pl.edu.uj.ii.skwarczek.betterchatfast.settings.SettingsContainerFragment
import pl.edu.uj.ii.skwarczek.betterchatfast.signin.AuthenticateViewModel
import pl.edu.uj.ii.skwarczek.betterchatfast.users.EMatchmakingStates
import pl.edu.uj.ii.skwarczek.betterchatfast.util.*
import java.util.*

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: AuthenticateViewModel = AuthenticateViewModel()
    private lateinit var auth: FirebaseAuth
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SendBirdCall instance to use APIs in your app.
        SendBirdCall.init(applicationContext, SENDBIRD_APP_ID)
        SendBirdCall.setLoggerLevel(SendBirdCall.LOGGER_INFO)
        SharedPreferencesManager.init(applicationContext)

        //Sendbird user auth
        auth = FirebaseAuth.getInstance()
        val mail = auth.currentUser?.email

        viewModel.authenticate(mail!!, null)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        observeViewModel()

        requestPermissions()

        getLocation()
    }

    override fun onResume() {
        super.onResume()
        FirestoreHelper.updateCurrentUserMatchmakingState(EMatchmakingStates.NOT_MATCHMAKING)
        FirestoreHelper.updateCurrentUserRoomId("")

    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    Log.d("locationTest",location.toString())
                    if (location != null) {
                        Log.d("locationTest","xd2")
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list: List<Address> =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val test = mutableListOf(list[0].latitude, list[0].longitude, list[0].countryName, list[0].locality, list[0].getAddressLine(0))
                        Log.d("locationTest", test.toString())
                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    private fun observeViewModel() {
        viewModel.authenticationLiveData.observe(this) { resource ->
            Log.d("SignInActivity", "observe() resource: $resource")
            when (resource.status) {
                Status.LOADING -> {
                    // TODO : show loading view
                }
                Status.SUCCESS -> {
                    binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
                    val tabLayout = binding.tabLayoutMain
                    tabLayout.addOnTabSelectedListener(onTabSelectedListener)

                    val viewPager2 = binding.viewPagerMain
                    viewPager2.adapter = ViewPagerAdapter(this)

                    TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
                        val iconResourceId = when (position) {
                            0 -> R.drawable.icon_rooms
                            1 -> R.drawable.icon_settings
                            else -> return@TabLayoutMediator
                        }

                        tab.setIcon(iconResourceId)
                    }.attach()
                }
                Status.ERROR -> Toast.makeText(this, resource.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            when (tab?.position) {
                0 -> tab.setIcon(R.drawable.icon_rooms_filled)
                1 -> tab.setIcon(R.drawable.icon_settings_filled)
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
            when (tab?.position) {
                0 -> tab.setIcon(R.drawable.icon_rooms_grey)
                1 -> tab.setIcon(R.drawable.icon_settings_grey)
            }
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {}
    }

    inner class ViewPagerAdapter(
        fragmentActivity: FragmentActivity
    ) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> DashboardFragment()
                1 -> SettingsContainerFragment()
                else -> throw IndexOutOfBoundsException()
            }
        }
    }
}
