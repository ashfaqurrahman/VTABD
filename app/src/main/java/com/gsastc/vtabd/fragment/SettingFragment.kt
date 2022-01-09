package com.gsastc.vtabd.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.gsastc.vtabd.*
import com.gsastc.vtabd.utils.logoutUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_setting.view.*

class SettingFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        requireActivity().bottom_navigation.menu.getItem(3).isChecked = true

        view?.developers?.setOnClickListener(this)
        view?.about_this_app?.setOnClickListener(this)
        view?.privacy_policy?.setOnClickListener(this)
        view?.faq?.setOnClickListener(this)
        view?.terms_conditions?.setOnClickListener(this)
        view?.log_out?.setOnClickListener(this)
        view?.version?.text = "Version: " + BuildConfig.VERSION_NAME

        return view
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.developers -> {
                startActivity(Intent(context, DevelopersActivity::class.java))
            }
            R.id.about_this_app -> {
                startActivity(Intent(context, AboutThisAppActivity::class.java))
            }
            R.id.privacy_policy -> {
                startActivity(Intent(context, PrivacyPolicyActivity::class.java))
            }
            R.id.faq -> {
                startActivity(Intent(context, FAQActivity::class.java))
            }
            R.id.terms_conditions -> {
                startActivity(Intent(context, TermsConditionsActivity::class.java))
            }
            R.id.log_out -> {
                logoutUser(requireContext())
            }
        }
    }
}
