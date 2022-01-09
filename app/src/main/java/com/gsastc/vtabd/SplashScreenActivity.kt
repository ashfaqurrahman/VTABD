package com.gsastc.vtabd

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager

@Suppress("DEPRECATION")
class SplashScreenActivity : AppCompatActivity() {

    private var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        context = this

        when (SharedPrefManager.getInstance(context!!)!!.getRegisterStatus) {
            "admin" -> {
                val intent = Intent(applicationContext, AdminPanelActivity::class.java)
                startActivity(intent)
                finish()
            }
            "user" -> {
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.putExtra("flag", 2)
                startActivity(intent)
                finish()
            }
            "guide" -> {
                startActivity(Intent(applicationContext, GuideActivity::class.java))
                finish()
            }
            "transport" -> {
                startActivity(Intent(applicationContext, TransportActivity::class.java))
                finish()
            }
            "hotel" -> {
                startActivity(Intent(applicationContext, HotelOwnerActivity::class.java))
                finish()
            }
            "author" -> {
                startActivity(Intent(applicationContext, AuthorActivity::class.java))
                finish()
            }
            "otp" -> {
                startActivity(Intent(applicationContext, RoleActivity::class.java))
                finish()
            }
            null -> {
                startActivity(Intent(applicationContext, PhoneActivity::class.java))
                finish()
            }
        }
    }
}
