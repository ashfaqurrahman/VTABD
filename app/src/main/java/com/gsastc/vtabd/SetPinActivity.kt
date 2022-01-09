package com.gsastc.vtabd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_set_pin.*

class SetPinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_pin)

        textField.setEndIconOnClickListener {
            Toast.makeText(applicationContext, "setEndIconOnClickListener 0", Toast.LENGTH_LONG).show()
        }

        textField.addOnEditTextAttachedListener {
            Toast.makeText(applicationContext, "addOnEditTextAttachedListener 1", Toast.LENGTH_LONG).show()
        }

        /*textField.addOnEndIconChangedListener {
            Toast.makeText(applicationContext, "addOnEditTextAttachedListener 2", Toast.LENGTH_LONG).show()
        }*/

        // Get input text
        val inputText1 = textField.editText?.text.toString()

        /*textField.editText?.doOnTextChanged { inputText1, _, _, _ ->
            // Respond to input text change
        }*/
    }
}