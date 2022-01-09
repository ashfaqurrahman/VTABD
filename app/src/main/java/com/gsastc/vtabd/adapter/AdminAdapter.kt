package com.gsastc.vtabd.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.gsastc.vtabd.fragment.*

@Suppress("DEPRECATION")
class TabAdapter(fm: FragmentManager, private var totalTabs: Int) : FragmentPagerAdapter(fm) {


    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> {
                fragment = PlaceFragment()
            }
            1 -> {
                fragment = HotelFragment()
            }
            2 -> {
                fragment = TransportFragment()
            }
            3 -> {
                fragment = GuideFragment()
            }
            4 -> {
                fragment = BlogFragment()
            }
        }
        return fragment!!
    }

    override fun getCount(): Int {
        return totalTabs
    }
}
