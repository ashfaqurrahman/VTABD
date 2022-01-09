package com.gsastc.vtabd


import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.gsastc.vtabd.model.DataModel
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_edit_profile.*
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.HashMap


@Suppress("DEPRECATION")
class EditProfileActivity : AppCompatActivity() {
    private var cropImageUri: Uri? = null
    private var downloadImageUri: Uri? = null
    private var PeopleImageRef: StorageReference? = null
    private var PeopleRef: DatabaseReference? = null
    private lateinit var mAuth: FirebaseAuth
    private var currentUserID: String? = null
    private var firstName: String? = null
    private var lastName: String? = null
    private var myEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val loading = ProgressDialog.show(
            this,
            "",
            "Please wait...",
            false,
            false
        )
        loading?.show()

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.currentUser?.uid
        PeopleImageRef = FirebaseStorage.getInstance().reference.child("people Images")
        PeopleRef = FirebaseDatabase.getInstance().reference.child("TotalPeople")

        val myData = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(DataModel::class.java)
                if (data?.userName != null) {
                    my_first_name.setText(data.userName.toString())
                }
                if (data?.userName != null) {
                    my_last_name.setText(data.userName.toString())
                }
                if (data?.myEmail != null) {
                    my_email.setText(data.myEmail.toString())
                }
                if (data?.imageUri != null) {
                    Picasso.get().load(data.imageUri).placeholder(R.drawable.ic_empty_profile).into(profile_pic)
                    downloadImageUri = Uri.parse(data.imageUri)
                }
                loading.dismiss()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    applicationContext,
                    "Please give your Bio",
                    Toast.LENGTH_LONG
                ).show()
                loading.dismiss()
            }
        }
        PeopleRef?.child(currentUserID.toString())?.addValueEventListener(myData)

        close.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("flag", 1)
            startActivity(intent)
            finish()
        }

        profile_pic?.setOnClickListener {
            CropImage.startPickImageActivity(this)
        }

        save_user_info?.setOnClickListener {
            firstName = my_first_name.text.toString()
            lastName = my_last_name.text.toString()
            myEmail = my_email.text.toString()
            if (firstName!!.isEmpty() || lastName!!.isEmpty() || myEmail!!.isEmpty()) {
                if (firstName!!.isEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        "Please give your first name",
                        Toast.LENGTH_LONG
                    ).show()
                }
                if (lastName!!.isEmpty()) {
                    Toast.makeText(
                        applicationContext,
                        "Please give tour last name",
                        Toast.LENGTH_LONG
                    ).show()
                }
                if (!isValidEmailId(myEmail.toString())) {
                    Toast.makeText(
                        applicationContext,
                        "Please give your email",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                val loadingSave = ProgressDialog.show(
                    this,
                    "Save Data",
                    "Please wait...",
                    false,
                    false
                )
                loadingSave.show()

                addUploadRecordToDb()
                loadingSave.dismiss()
            }
        }
    }

    private fun isValidEmailId(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-10]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }

    private fun uploadImage() {

    }

    private fun addUploadRecordToDb() {
        val data = HashMap<String, Any>()
        data["imageUrl"] = downloadImageUri.toString()
        data["firstName"] = firstName.toString()
        data["lastName"] = lastName.toString()
        data["myEmail"] = myEmail.toString()

        PeopleRef?.child(currentUserID.toString())?.updateChildren(data)?.addOnSuccessListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("flag", 1)
            startActivity(intent)
            finish()
            Toast.makeText(this, "Successfully update your info", Toast.LENGTH_LONG).show()
        }?.addOnFailureListener {
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
                profile_pic.setImageURI(result.uri)
                cropImageUri = result.uri
                if (cropImageUri != null) {
                    val ref = PeopleImageRef?.child("uploads/" + UUID.randomUUID().toString())
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
            .start(this)
    }
}
