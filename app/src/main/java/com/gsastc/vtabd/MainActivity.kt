package com.gsastc.vtabd

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gsastc.vtabd.adapter.DatabaseHelper
import com.gsastc.vtabd.fragment.*
import java.util.*


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {

    private val bundle = Bundle()
    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val homeFragment = HomeFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myDB = DatabaseHelper(this)
        val data = myDB.listContents
        val navBar  = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (data!!.count == 0) {
            navBar.getOrCreateBadge(R.id.cart).number = 0
        }
        else {
            navBar.getOrCreateBadge(R.id.cart).number = data.count
        }

        val pref: SharedPreferences = this.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val flag = intent!!.getIntExtra("flag", 0)
        Log.e("Flag", flag.toString())

        when {
            // come from branch of more fragment
            flag == 1 -> {
                loadFragment(WishListFragment())
            }
            // come from splash screen
            flag == 2 -> {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                getLastLocation()
            }
            // come from hotel details
            flag == 3 -> {
                loadFragment(CartFragment())
            }
            // come from otp page
            SharedPrefManager.getInstance(this)?.getDistrict == null || SharedPrefManager.getInstance(this)?.getDivision == null -> {
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                getLastLocation()
            }
            // come from others page
            else -> {
                loadFragment(HomeFragment())
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragmentToBack(HomeFragment())
                }
                R.id.cart -> {
                    loadFragmentToBack(CartFragment())
                }
                R.id.history -> {
                    loadFragmentToBack(HistoryFragment())
                }
                R.id.settings -> {
                    loadFragmentToBack(SettingFragment())
                }

            }
            true
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        val cityName = addresses[0].getAddressLine(0)

                        val district = addresses[0].subAdminArea
                        val districtArray = district.split(" ")

                        val division = addresses[0].adminArea
                        val divisionArray = division.split(" ")

                        SharedPrefManager.getInstance(this)!!.saveDistrictDivision(districtArray[0], divisionArray[0])
                        loadFragment(homeFragment)

                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            //findViewById<TextView>(com.google.android.gms.location.R.id.latTextView).text = mLastLocation.latitude.toString()
            //findViewById<TextView>(com.google.android.gms.location.R.id.lonTextView).text = mLastLocation.longitude.toString()
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
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
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            } else {
                finish()
                Log.e("SSSSS", "Deny")
            }
        }
    }

    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
            //additional code
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    private fun loadFragment(fragment: Fragment) { // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.commit()
    }

    private fun loadFragmentToBack(fragment: Fragment) { // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
