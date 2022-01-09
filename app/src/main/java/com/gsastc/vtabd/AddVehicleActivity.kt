package com.gsastc.vtabd

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.gsastc.vtabd.utils.toasty
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_add_vehicle.*
import java.util.*

class AddVehicleActivity : AppCompatActivity() {

    private var cropImageUri: Uri? = null
    private var vehicleImageRef: StorageReference? = null
    private var vehicleRef: DatabaseReference? = null
    private lateinit var mAuth: FirebaseAuth
    private var currentUserID: String? = ""
    private var hotel: String? = null
    private var vehicleModelNane: String? = null
    private var desc: String? = null
    private var capacity: String? = null
    private var price: String? = null
    private var ac: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vehicle)

        val mToolbar = findViewById<MaterialToolbar>(R.id.vehicle_toolbar)
        setSupportActionBar(mToolbar)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        val toolbarTv = findViewById<TextView>(R.id.vehicle_tv)
        toolbarTv.text = "Enter Vehicle info"

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.currentUser?.uid
        vehicleImageRef = FirebaseStorage.getInstance().reference.child("Vehicle Images")
        vehicleRef = FirebaseDatabase.getInstance().reference.child("vehicle")

        hotel = SharedPrefManager.getInstance(this@AddVehicleActivity)!!.getHotelName

        vehicle_image?.setOnClickListener {
            CropImage.startPickImageActivity(this)
        }

        submit?.setOnClickListener {
            vehicleModelNane = vehicle_model_name.text.toString()
            desc = vehicle_description.text.toString()
            capacity = vehicle_capacity.text.toString()
            price = vehicle_price.text.toString()
            if (cropImageUri == null || vehicleModelNane == null || desc == null || capacity == null || price == null || ac == null) {
                when {
                    cropImageUri == null -> {
                        vehicle_image?.background?.setColorFilter(
                            resources.getColor(R.color.red),
                            PorterDuff.Mode.SRC_ATOP
                        )
                        Toast.makeText(
                            applicationContext,
                            "Please give vehicle image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    vehicleModelNane!!.isEmpty() -> {
                        vehicle_model_name_layout.error = "Enter vehicle model name"
                    }
                    desc!!.isEmpty() -> {
                        vehicle_desc_layout.error = "Enter vehicle description"
                    }
                    capacity!!.isEmpty() -> {
                        vehicle_capacity_layout.error = "Enter vehicle capacity"
                    }
                    price!!.isEmpty() -> {
                        vehicle_price_layout.error = "Enter vehicle price per night"
                    }
                    ac!!.isEmpty() -> {
                        Toast.makeText(
                            applicationContext,
                            "Have any AC service in vehicle?",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                val loading = ProgressDialog.show(
                    this,
                    "",
                    "Please wait...",
                    false,
                    false
                )
                loading?.show()
                uploadImage(loading)
            }
        }
    }

    private fun uploadImage(loading: ProgressDialog) {
        if (cropImageUri != null) {
            val ref = vehicleImageRef?.child("uploads/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(cropImageUri!!)

            val urlTask =
                uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation ref.downloadUrl
                })?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result
                        addUploadRecordToDb(downloadUri.toString(), loading)
                    } else {
                        // Handle failures
                    }
                }?.addOnFailureListener {

                }
        } else {
            Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addUploadRecordToDb(uri: String, loading: ProgressDialog) {
        val vehicleKey = vehicleRef?.push()?.key
        val data = HashMap<String, Any>()
        data["transportID"] = currentUserID.toString()
        data["transportName"] = hotel.toString()
        data["vehicleKey"] = vehicleKey.toString()
        data["imageUrl"] = uri
        data["vehicleModelNane"] = vehicleModelNane.toString()
        data["description"] = desc.toString()
        data["capacity"] = capacity.toString()
        data["price"] = price.toString()
        data["ac"] = ac.toString()
        data["cityName"] = intent.getStringExtra("city").toString()
        data["visibility"] = "Show"
        vehicleRef?.child(vehicleKey!!)?.setValue(data)?.addOnSuccessListener {
            toasty(this, "You add new vehicle successfully")
            val intent = Intent(applicationContext, TransportActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            loading.dismiss()
        }?.addOnFailureListener {
            loading.dismiss()
            Toast.makeText(this, "Error saving to DB", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageUri = CropImage.getPickImageResultUri(this, data)
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageUri)) {
                cropImageUri = imageUri
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                startCropImageActivity(imageUri)
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                vehicle_image.setImageURI(result.uri)
                cropImageUri = result.uri
                vehicle_image?.background?.setColorFilter(
                    resources.getColor(R.color.edittext_border),
                    PorterDuff.Mode.SRC_ATOP
                )
                vehicle_image?.background?.setColorFilter(
                    resources.getColor(R.color.edittext_border),
                    PorterDuff.Mode.SRC_ATOP
                )
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.error, Toast.LENGTH_SHORT).show()
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
            .setAspectRatio(3, 2)
            .start(this)
    }

    fun selectAC(view: View) {
        when (ac_radio_group.checkedRadioButtonId) {
            R.id.ac_yes -> {
                ac = "Yes"
            }
            R.id.ac_no -> {
                ac = "No"
            }
        }
    }
}