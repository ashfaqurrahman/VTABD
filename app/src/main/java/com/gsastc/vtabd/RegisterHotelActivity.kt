package com.gsastc.vtabd

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.gsastc.vtabd.utils.isValidEmailId
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_register_hotel.*
import java.util.*

class RegisterHotelActivity : AppCompatActivity() {
    private var cropImageUri: Uri? = null
    private var hotelImageRef: StorageReference? = null
    private var hotelRef: DatabaseReference? = null
    private var peopleRef: DatabaseReference? = null
    private var cityName: String? = null
    private var divisionName: String? = null
    private var currentUserID: String? = null
    private var hotelName: String? = null
    private var hotelMail: String? = null
    private var hotelDescription: String? = null
    lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_hotel)

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.currentUser?.uid
        hotelImageRef = FirebaseStorage.getInstance().reference.child("Hotel Images")
        hotelRef = FirebaseDatabase.getInstance().reference.child("Hotel")
        peopleRef = FirebaseDatabase.getInstance().reference.child("people")

        city_image?.setOnClickListener {
            CropImage.startPickImageActivity(this)
        }

        val division = arrayOf(
            "Select Division",
            "Barisal",
            "Chittagong",
            "Dhaka",
            "Khulna",
            "Mymensingh",
            "Rajshahi",
            "Rangpur",
            "Sylhet"
        )

        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, division)
        division_name.adapter = arrayAdapter
        division_name.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                setHotel(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }

        submit?.setOnClickListener {
            hotelName = hotel_name.text.toString()
            hotelMail = email.text.toString()
            hotelDescription = hotel_description.text.toString()
            divisionName = division_name.selectedItem.toString()
            cityName = city_name?.selectedItem.toString()
            if (cropImageUri == null || hotelName!!.isEmpty() || hotelMail!!.isEmpty() || hotelDescription!!.isEmpty() || divisionName == "Select Division" || cityName == "Select City") {
                when {
                    cropImageUri == null -> {
                        city_image?.background?.setColorFilter(
                            resources.getColor(R.color.red),
                            PorterDuff.Mode.SRC_ATOP
                        )
                        Toast.makeText(
                            applicationContext,
                            "Please give Hotel image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    hotelName!!.isEmpty() -> {
                        hotel_name_layout.error = "Enter your hotel name"
                    }

                    hotelMail!!.isEmpty() -> {
                        hotel_name_layout.error = "Empty email"
                    }

                    !isValidEmailId(hotelMail!!) -> {
                        email_layout.error = "Enter valid email"
                    }

                    hotelDescription!!.isEmpty() -> {
                        email_layout.error = "Enter hotel description"
                    }

                    divisionName == "Select Division" -> {
                        division_name?.background?.setColorFilter(
                            resources.getColor(R.color.red),
                            PorterDuff.Mode.SRC_ATOP
                        )
                        Toast.makeText(
                            applicationContext,
                            "Please select a division",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    cityName == "Select City" -> {
                        city_name?.background?.setColorFilter(
                            resources.getColor(R.color.red),
                            PorterDuff.Mode.SRC_ATOP
                        )
                        Toast.makeText(
                            applicationContext,
                            "Please select a city",
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

    private fun uploadImage(loading : ProgressDialog) {
        if (cropImageUri != null) {
            val ref = hotelImageRef?.child("uploads/" + UUID.randomUUID().toString())
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

    private fun setHotel(position: Int) {
        val barisal = arrayOf(
            "Select City",
            "Barisal",
            "Barguna",
            "Bhola",
            "Jhalokati",
            "Patuakhali",
            "Pirojpur"
        )
        val chittagong = arrayOf(
            "Select City",
            "Bandarban",
            "Brahmanbari",
            "Chandpur",
            "Chittagong",
            "Comilla",
            "Cox's Bazar",
            "Feni",
            "Khagrachari",
            "Lakshmipur",
            "Noakhali",
            "Rangamati"
        )
        val dhaka = arrayOf(
            "Select City",
            "Dhaka",
            "Faridpur",
            "Gazipur",
            "Gopalganj",
            "Gopalganj",
            "Kishoreganj",
            "Madaripur",
            "Manikganj",
            "Munshiganj",
            "Narayanganj",
            "Narsingdi",
            "Rajbari",
            "Shariatpur",
            "Tangai"
        )
        val khulna = arrayOf(
            "Select City",
            "Bagerhat",
            "Chuadanga",
            "Jessore",
            "Jhenaidah",
            "Khulna",
            "Kushtia",
            "Magura",
            "Meherpur",
            "Narail",
            "Satkhira"
        )
        val mymensingh = arrayOf("Select City", "Jamalpur", "Mymensingh", "Netrokona", "Sherpur")
        val rajshahi = arrayOf(
            "Select City",
            "Bogra",
            "Jaipurhat",
            "Naogaon",
            "Natore",
            "Nawabganj",
            "Pabna",
            "Rajshahi",
            "Sirajganj"
        )
        val rangpur = arrayOf(
            "Select City",
            "Dinajpur",
            "Gaibandha",
            "Kurigram",
            "Lalmonirhat",
            "Nilphamari",
            "Panchagarh",
            "Rangpur",
            "Thakurgaon"
        )
        val sylhet = arrayOf("Select City", "Habiganj", "Moulvibazar", "Sunamganj", "Sylhet")

        when (position) {
            1 -> {
                val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, barisal)
                city_name.adapter = arrayAdapter
            }
            2 -> {
                val arrayAdapter =
                    ArrayAdapter(this, android.R.layout.simple_list_item_1, chittagong)
                city_name.adapter = arrayAdapter
            }
            3 -> {
                val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, dhaka)
                city_name.adapter = arrayAdapter
            }
            4 -> {
                val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, khulna)
                city_name.adapter = arrayAdapter
            }
            5 -> {
                val arrayAdapter =
                    ArrayAdapter(this, android.R.layout.simple_list_item_1, mymensingh)
                city_name.adapter = arrayAdapter
            }
            6 -> {
                val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, rajshahi)
                city_name.adapter = arrayAdapter
            }
            7 -> {
                val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, rangpur)
                city_name.adapter = arrayAdapter
            }
            8 -> {
                val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, sylhet)
                city_name.adapter = arrayAdapter
            }
        }

        if (position != 0) {
            city_option.visibility = View.VISIBLE
            division_name?.background?.setColorFilter(
                resources.getColor(R.color.edittext_border),
                PorterDuff.Mode.SRC_ATOP
            )
        }
        city_name.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                city_name?.background?.setColorFilter(
                    resources.getColor(R.color.edittext_border),
                    PorterDuff.Mode.SRC_ATOP
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }
    }

    private fun addUploadRecordToDb(uri: String, loading : ProgressDialog) {
        val data = HashMap<String, Any>()
        data["imageUrl"] = uri
        data["hotelName"] = hotelName.toString()
        data["hotelMail"] = hotelMail.toString()
        data["hotelPhone"] = SharedPrefManager.getInstance(this)?.getPhone!!
        data["hotelDescription"] = hotelDescription.toString()
        data["cityName"] = cityName.toString()
        data["division"] = divisionName.toString()
        data["hotelID"] = currentUserID.toString()

        hotelRef?.child(currentUserID!!)?.setValue(data)?.addOnSuccessListener {
            val user = HashMap<String, Any>()
            user["role"] = "hotel"
            user["name"] = hotelName.toString()
            user["phone"] = SharedPrefManager.getInstance(this)?.getPhone!!
            peopleRef?.child(currentUserID!!)?.setValue(user)?.addOnSuccessListener {
                SharedPrefManager.getInstance(this)?.saveRegisterStatus("hotel")
                val intent = Intent(applicationContext, HotelOwnerActivity::class.java)
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
                Log.e("AAAA", cropImageUri.toString())
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                startCropImageActivity(imageUri)
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                city_image?.setImageURI(result.uri)
                cropImageUri = result.uri
                city_image?.background?.setColorFilter(
                    resources.getColor(R.color.edittext_border),
                    PorterDuff.Mode.SRC_ATOP
                )
                city_image?.background?.setColorFilter(
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
            .setAspectRatio(3,2)
            .start(this)
    }
}