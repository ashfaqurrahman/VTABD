package com.gsastc.vtabd

import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import kotlinx.android.synthetic.main.activity_phone.*

class PhoneActivity : AppCompatActivity() {
    var number : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone)

        if (SharedPrefManager.getInstance(this)?.getPhone != null) {
            phone.setText(SharedPrefManager.getInstance(this)?.getPhone)
            number = SharedPrefManager.getInstance(this)?.getPhone
            outlinedTextField.error = null
            next?.background?.setColorFilter(
                resources.getColor(R.color.colorPrimary),
                PorterDuff.Mode.SRC_ATOP)
            next?.setTextColor(Color.parseColor("#FFFFFF"))
            next?.isClickable = true
        }

        next?.isClickable = false
        phone.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                number = s.toString()
                if (s.length == 11) {
                    if (s.startsWith("017") || s.startsWith("013") || s.startsWith("019") || s.startsWith("014") || s.startsWith("018") || s.startsWith("015") || s.startsWith("016")) {
                        outlinedTextField.error = null
                        next?.background?.setColorFilter(
                            resources.getColor(R.color.colorPrimary),
                            PorterDuff.Mode.SRC_ATOP)
                        next?.setTextColor(Color.parseColor("#FFFFFF"))
                        next?.isClickable = true
                    }
                }
                else {
                    outlinedTextField.error = "Enter Correct Number!"
                    outlinedTextField.boxStrokeErrorColor = resources.getColorStateList(R.color.half_black)
                    outlinedTextField.hintTextColor = resources.getColorStateList(R.color.half_black)
                    next?.background = resources.getDrawable(R.drawable.button_selector_white)
                    next?.setTextColor(Color.parseColor("#CEDCDC"))
                    next?.isClickable = false
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int,
                                           after: Int) { // TODO Auto-generated method stub
            }

            override fun afterTextChanged(s: Editable) {

            }
        })

        next?.setOnClickListener {
            val intent = Intent(this, OTPActivity::class.java)
            intent.putExtra("number", "+88$number")
            SharedPrefManager.getInstance(this)?.savePhone(number)
            startActivity(intent)
        }

    }
}