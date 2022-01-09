package com.gsastc.vtabd

import `in`.aabhasjindal.otptextview.OTPListener
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gsastc.vtabd.model.DataModel
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_o_t_p.*
import kotlinx.android.synthetic.main.activity_phone.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class OTPActivity : AppCompatActivity() {

    private var verificationId: String? = null
    private var mAuth: FirebaseAuth? = null
    var otp1: String? = null
    var number:String? = null
    var timer: CountDownTimer? = null
    private var loading: ProgressDialog? = null

    var currentUserID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_o_t_p)

        setSupportActionBar(toolbar_main)
        val actionbar = supportActionBar
        toolbar_main_text.text = "Verify Your Phone Number"
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setHomeAsUpIndicator(resources.getDrawable(R.drawable.ic_arrow_back))

        mAuth = FirebaseAuth.getInstance()

        number = intent.getStringExtra("number")
        show_phone_no?.text = number

        edit_phone?.setOnClickListener {
            val intent = Intent(this, PhoneActivity::class.java)
            intent.flags = (Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        code_status?.text = "Wait for resend the code  "
        sendOTP()

        resend?.setOnClickListener {
            sendOTP()
        }

        authentication?.setOnClickListener {
            val verifiNo = otp1
            if (verifiNo != null){
                verifyCode(verifiNo)
            }
        }

        otp_view.otpListener = object : OTPListener {
            override fun onInteractionListener() {
                otp_view.resetState()
            }

            override fun onOTPComplete(otp: String) {
                otp1 = otp
                authentication?.background?.setColorFilter(
                    resources.getColor(R.color.colorPrimary),
                    PorterDuff.Mode.SRC_ATOP)
                authentication?.isClickable = true
            }
        }
    }

    private fun sendOTP(){
        timer()
        sendVerificationCode(number!!)
    }

    private fun sendVerificationCode(number: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            number,
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            mCallBack
        )
    }

    private val mCallBack: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)
                verificationId = s

            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    otp_view.setOTP(code)
                    verifyCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@OTPActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }

    private fun verifyCode(code: String) {
        loading = ProgressDialog.show(
            this,
            "Verifying",
            "Please wait...",
            false,
            false
        )
        loading?.show()
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithCredential(credential)
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (number == "+8801755467748") {
                        SharedPrefManager.getInstance(this@OTPActivity)?.saveRegisterStatus("admin")
                        val intent = Intent(this@OTPActivity, AdminPanelActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    else {
                        checkUser()
                    }
                } else {
                    Toast.makeText(
                        this@OTPActivity,
                        task.exception!!.message,
                        Toast.LENGTH_LONG
                    ).show()
                    loading?.dismiss()
                }
            }
    }

    private fun checkUser() {
        currentUserID = mAuth?.currentUser?.uid
        val ref = FirebaseDatabase.getInstance().reference.child("people/$currentUserID")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                Log.d("dataSnapShotValue",""+dataSnapshot.value)
                // User exist
                val data = dataSnapshot.getValue(DataModel::class.java)
                when (data?.role) {
                    "user" -> {
                        SharedPrefManager.getInstance(this@OTPActivity)?.saveRegisterStatus("user")
                        val intent = Intent(this@OTPActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                        loading?.dismiss()
                    }
                    "guide" -> {
                        SharedPrefManager.getInstance(this@OTPActivity)?.saveRegisterStatus("guide")
                        val intent = Intent(this@OTPActivity, GuideActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                        loading?.dismiss()
                    }
                    "transport" -> {
                        SharedPrefManager.getInstance(this@OTPActivity)?.saveRegisterStatus("transport")
                        val intent = Intent(this@OTPActivity, TransportActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                        loading?.dismiss()
                    }
                    "hotel" -> {
                        SharedPrefManager.getInstance(this@OTPActivity)?.saveRegisterStatus("hotel")
                        val intent = Intent(this@OTPActivity, HotelOwnerActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                        loading?.dismiss()
                    }
                    "author" -> {
                        SharedPrefManager.getInstance(this@OTPActivity)?.saveRegisterStatus("author")
                        val intent = Intent(this@OTPActivity, AuthorActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                        loading?.dismiss()
                    }
                    else -> {
                        val intent = Intent(this@OTPActivity, RoleActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                        loading?.dismiss()
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                println("The read failed: " + databaseError.code)
            }
        })
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun timer() {
        resend.isClickable = false
        timer = object: CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                resend.text = " " + (millisUntilFinished/1000) + "s"
            }

            override fun onFinish() {
                code_status?.text = "Did not receive the code?"
                resend.text = " Resend Code"
                resend.isClickable = true
            }
        }
        timer?.start()
    }
}
