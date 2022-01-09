package com.gsastc.vtabd

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_role.*

class RoleActivity : AppCompatActivity() {
    var role : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role)

        /*setSupportActionBar(toolbar_main)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        toolbar_main_text.text = intent.getStringExtra("nameBan")*/

        select?.setOnClickListener {
            when(role) {
                "User" -> {
                    val intent = Intent(applicationContext, RegisterActivity::class.java)
                    intent.putExtra("number", intent.getStringExtra("number"))
                    startActivity(intent)
                }
                "Guide" -> {
                    val intent = Intent(applicationContext, RegisterGuideActivity::class.java)
                    intent.putExtra("number", intent.getStringExtra("number"))
                    startActivity(intent)
                }
                "Transport" -> {
                    val intent = Intent(applicationContext, RegisterTransportActivity::class.java)
                    intent.putExtra("number", intent.getStringExtra("number"))
                    startActivity(intent)
                }
                "Hotel" -> {
                    val intent = Intent(applicationContext, RegisterHotelActivity::class.java)
                    intent.putExtra("number", intent.getStringExtra("number"))
                    startActivity(intent)
                }
                "Author" -> {
                    val intent = Intent(applicationContext, RegisterAuthorActivity::class.java)
                    intent.putExtra("number", intent.getStringExtra("number"))
                    startActivity(intent)
                }
            }
        }
    }

    fun selectRole(view : View) {
        when(radio_group.checkedRadioButtonId) {
            R.id.user -> {
                role = "User"
                clickableButton()
            }
            R.id.guide -> {
                role = "Guide"
                clickableButton()
            }
            R.id.rider -> {
                role = "Transport"
                clickableButton()
            }
            R.id.hotel -> {
                role = "Hotel"
                clickableButton()
            }
            R.id.author -> {
                role = "Author"
                clickableButton()
            }
        }
    }

    private fun clickableButton() {
        select?.isClickable = true
        select?.background?.setColorFilter(
            resources.getColor(R.color.colorPrimary),
            PorterDuff.Mode.SRC_ATOP)
        select?.setTextColor(Color.parseColor("#FFFFFF"))
    }
}