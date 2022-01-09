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
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_register_transport.*
import java.util.*
import kotlin.collections.HashMap

class RegisterTransportActivity : AppCompatActivity() {

    private var downloadImageUri: Uri? = null
    private var peopleImageRef: StorageReference? = null
    private var peopleRef: DatabaseReference? = null
    private var transportRef: DatabaseReference? = null
    private lateinit var mAuth: FirebaseAuth
    private var currentUserID: String? = null
    private var cropImageUri: Uri? = null
    private var transportName: String? = null
    private var cityName: String? = null
    private var divisionName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_transport)

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.currentUser?.uid
        peopleImageRef = FirebaseStorage.getInstance().reference.child("Transport Images")
        peopleRef = FirebaseDatabase.getInstance().reference.child("people")
        transportRef = FirebaseDatabase.getInstance().reference.child("transport")

        transport_image?.setOnClickListener {
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
                setAuthor(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }

        submit?.setOnClickListener {
            transportName = agent_name.text.toString()
            cityName = city_name?.selectedItem.toString()
            divisionName = division_name.selectedItem.toString()
            if (downloadImageUri == null || transportName!!.isEmpty() || cityName == "Select City" || divisionName == "Select Division") {
                when {
                    downloadImageUri == null -> {
                        Toast.makeText( this, "Enter your profile pic", Toast.LENGTH_SHORT).show()
                    }

                    transportName!!.isEmpty() -> {
                        transport_name_layout.error = "Enter agent name"
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

    private fun setAuthor(position: Int) {
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

    private fun addUploadRecordToDb(loading : ProgressDialog) {
        val data = HashMap<String, Any>()
        data["transportID"] = currentUserID!!
        data["imageUrl"] = downloadImageUri.toString()
        data["transportName"] = transportName.toString()
        data["cityName"] = cityName.toString()
        data["division"] = divisionName.toString()
        data["phone"] = SharedPrefManager.getInstance(this)?.getPhone!!
        transportRef?.child(currentUserID!!)?.updateChildren(data)?.addOnSuccessListener {
            val user = HashMap<String, Any>()
            user["role"] = "transport"
            user["name"] = transportName.toString()
            user["phone"] = SharedPrefManager.getInstance(this)?.getPhone!!
            peopleRef?.child(currentUserID!!)?.updateChildren(user)?.addOnSuccessListener {
                SharedPrefManager.getInstance(this)?.saveRegisterStatus("transport")
                val intent = Intent(this, TransportActivity::class.java)
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
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                startCropImageActivity(imageUri)
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                transport_image?.setImageURI(result.uri)
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
            .setAspectRatio(3,2)
            .start(this)
    }
}