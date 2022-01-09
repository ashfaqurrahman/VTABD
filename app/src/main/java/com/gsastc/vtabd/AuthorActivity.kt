package com.gsastc.vtabd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gsastc.vtabd.fragment.AuthorDashboardFragment
import com.gsastc.vtabd.fragment.AuthorProfileFragment

class AuthorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_author)

        loadFragment(AuthorDashboardFragment())

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.author_dashboard -> {
                    loadFragmentToBack(AuthorDashboardFragment())
                }
                R.id.author_profile -> {
                    loadFragmentToBack(AuthorProfileFragment())
                }
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) { // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.author_frame_container, fragment)
        transaction.commit()
    }

    private fun loadFragmentToBack(fragment: Fragment) { // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.author_frame_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}