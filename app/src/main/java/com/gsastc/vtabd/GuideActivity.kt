package com.gsastc.vtabd

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gsastc.vtabd.fragment.*


class GuideActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        loadFragment(GuideDashboardFragment())

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.guide_dashboard -> {
                    loadFragmentToBack(GuideDashboardFragment())
                }
                R.id.guide_profile -> {
                    loadFragmentToBack(GuideProfileFragment())
                }
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) { // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.guide_frame_container, fragment)
        transaction.commit()
    }

    private fun loadFragmentToBack(fragment: Fragment) { // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.guide_frame_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}