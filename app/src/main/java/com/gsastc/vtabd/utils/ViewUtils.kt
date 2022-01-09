package com.gsastc.vtabd.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.firebase.database.DatabaseReference
import com.gsastc.vtabd.PhoneActivity
import com.gsastc.vtabd.R
import com.gsastc.vtabd.SharedPrefManager
import java.util.regex.Pattern

fun hideKeyboard(activity: Activity) {
    val imm: InputMethodManager =
        activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = activity.currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(activity)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun toasty(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun successfulDialog(context: Context, _title: String, _desc: String) {
    val successDialog = Dialog(context)
    successDialog.setContentView(R.layout.successful_dialog)

    val title = successDialog.findViewById<TextView>(R.id.title)
    val desc = successDialog.findViewById<TextView>(R.id.desc)
    val ok = successDialog.findViewById<TextView>(R.id.ok)

    ok.setOnClickListener {
        successDialog.dismiss()
    }

    title.text = _title
    desc.text = _desc

    successDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    successDialog.window
        ?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

    successDialog.show()
}

fun alertDialog(context: Context, message: String, ref: DatabaseReference? = null) {
    val dialogs = Dialog(context)
    dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialogs.setContentView(R.layout.alert_dialog_with_mgs_y_n)
    dialogs.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialogs.window!!.setLayout(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    dialogs.setCancelable(true)
    dialogs.findViewById<TextView>(R.id.message).text = message
    dialogs.findViewById<TextView>(R.id.cancel).setOnClickListener {
        dialogs.dismiss()
    }
    dialogs.findViewById<TextView>(R.id.yesBtn).setOnClickListener {
        dialogs.dismiss()
        ref?.removeValue()
        successfulDialog(context, "Successful", "Delete successfully")
    }

    dialogs.show()
}

fun logoutUser(context: Context) {
    val dialogs = Dialog(context)
    dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE)
    dialogs.setContentView(R.layout.logout_layout)
    dialogs.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    dialogs.window!!.setLayout(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    dialogs.setCancelable(true)
    dialogs.findViewById<TextView>(R.id.cancel).setOnClickListener {
        dialogs.dismiss()
    }
    dialogs.findViewById<TextView>(R.id.yesBtn).setOnClickListener {
        SharedPrefManager.getInstance(context)?.saveRegisterStatus(null)
        SharedPrefManager.getInstance(context)?.saveCurrentLocation(null)
        SharedPrefManager.getInstance(context)?.saveUserNamePic(null, null)
        SharedPrefManager.getInstance(context)?.saveHotelImageNameAddress(null, null, null)
        SharedPrefManager.getInstance(context)?.saveRoomPersonChild(0, 0, 0)
        SharedPrefManager.getInstance(context)?.saveDateRange(null)
        val intent = Intent(context, PhoneActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    dialogs.show()
}

fun isValidEmailId(email: String): Boolean {
    return Pattern.compile(
        "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-10]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
    ).matcher(email).matches()
}
