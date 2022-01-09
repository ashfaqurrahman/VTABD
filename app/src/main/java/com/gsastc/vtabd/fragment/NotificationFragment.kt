package com.gsastc.vtabd.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.gsastc.vtabd.R
import kotlinx.android.synthetic.main.activity_main.*

class NotificationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notification, container, false)

        activity!!.bottom_navigation.menu.getItem(2).isChecked = true

        return view
    }

}
