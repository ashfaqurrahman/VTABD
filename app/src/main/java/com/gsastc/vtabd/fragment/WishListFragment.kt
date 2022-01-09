package com.gsastc.vtabd.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gsastc.vtabd.R
import kotlinx.android.synthetic.main.activity_main.*

class WishListFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_wish_list, container, false)

        requireActivity().bottom_navigation.menu.getItem(3).isChecked = true



        return view
    }

}