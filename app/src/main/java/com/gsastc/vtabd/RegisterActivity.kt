package com.gsastc.vtabd

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.gsastc.vtabd.utils.isValidEmailId
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity() {
    private var cropImageUri: Uri? = null
    private var downloadImageUri: Uri? = null
    private var peopleImageRef: StorageReference? = null
    private var peopleRef: DatabaseReference? = null
    private var userRef: DatabaseReference? = null
    private lateinit var mAuth: FirebaseAuth
    private var currentUserID: String? = null
    private var username: String? = null
    private var userEmail: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.currentUser?.uid
        peopleImageRef = FirebaseStorage.getInstance().reference.child("people Images")
        peopleRef = FirebaseDatabase.getInstance().reference.child("people")
        userRef = FirebaseDatabase.getInstance().reference.child("users")

        profile_pic?.setOnClickListener {
            CropImage.startPickImageActivity(this)
        }

        submit?.setOnClickListener {
            username = name.text.toString()
            userEmail = email.text.toString()
            if (downloadImageUri == null || username!!.isEmpty() || userEmail!!.isEmpty()) {
                if (downloadImageUri == null) {
                    Toast.makeText( this, "Enter your profile pic", Toast.LENGTH_SHORT).show()
                }
                else if (username!!.isEmpty()) {
                    name_layout.error = "Enter your name"
                }
                else if (!isValidEmailId(userEmail.toString())) {
                    email_layout.error = "Enter valid email"
                }
            } else {
                val loadingSave = ProgressDialog.show(
                    this,
                    "Creating Account",
                    "Please wait...",
                    false,
                    false
                )
                loadingSave.show()

                addUploadRecordToDb(loadingSave)
            }
        }
    }

    private fun addUploadRecordToDb(loading : ProgressDialog) {
        Log.e("EEEE E", SharedPrefManager.getInstance(this)?.getPhone!!)
        val data = HashMap<String, Any>()
        data["uid"] = currentUserID!!
        data["imageUri"] = downloadImageUri.toString()
        data["userName"] = username.toString()
        data["userEmail"] = userEmail.toString()
        data["userPhone"] = SharedPrefManager.getInstance(this)?.getPhone!!
        userRef?.child(currentUserID!!)?.updateChildren(data)?.addOnSuccessListener {
            val user = HashMap<String, Any>()
            user["role"] = "user"
            user["name"] = username.toString()
            user["phone"] = SharedPrefManager.getInstance(this)?.getPhone!!
            peopleRef?.child(currentUserID!!)?.updateChildren(user)?.addOnSuccessListener {
                SharedPrefManager.getInstance(this)?.saveRegisterStatus("user")
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                loading.dismiss()
            }?.addOnFailureListener {
                loading.dismiss()
                Toast.makeText(this, "Error saving to DB", Toast.LENGTH_LONG).show()
            }
        }?.addOnFailureListener {
            loading.dismiss()
            Toast.makeText(this, "Error saving to DB", Toast.LENGTH_LONG).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri = CropImage.getPickImageResultUri(this, data)
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                cropImageUri = imageUri
                Log.e("AAAA", cropImageUri.toString())
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                startCropImageActivity(imageUri)
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                profile_pic?.setImageURI(result.uri)
                cropImageUri = result.uri
                if (cropImageUri != null) {
                    val ref = peopleImageRef?.child("uploads/" + UUID.randomUUID().toString())
                    val uploadTask = ref?.putFile(cropImageUri!!)
                    val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        return@Continuation ref.downloadUrl
                    })?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            downloadImageUri = task.result
                        } else {
                            // Handle failures
                        }
                    }?.addOnFailureListener {

                    }
                } else {
                    Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.error, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (cropImageUri != null && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCropImageActivity(cropImageUri!!)
        } else {
            Toast.makeText(
                this,
                "Cancelling, required permissions are not granted",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun startCropImageActivity(imageUri: Uri?) {
        CropImage.activity(imageUri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMultiTouchEnabled(true)
            .setAspectRatio(1, 1)
            .start(this)
    }
}