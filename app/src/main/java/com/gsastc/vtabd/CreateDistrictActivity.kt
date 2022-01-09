package com.gsastc.vtabd

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
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
import com.gsastc.vtabd.utils.toasty
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_create_district.*
import java.util.*
import kotlin.collections.HashMap

@Suppress("DEPRECATION", "NAME_SHADOWING")
class CreateDistrictActivity : AppCompatActivity() {


    private var cropImageUri: Uri? = null
    private var districtImageRef: StorageReference? = null
    private var districtRef: DatabaseReference? = null
    private var districtName: String? = null
    private var divisionName: String? = null
    private var currentUserID: String? = null
    private var description: String? = null
    lateinit var mAuth: FirebaseAuth
    private var flag = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_district)

        close_city?.setOnClickListener {
            finish()
        }

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.currentUser?.uid
        districtImageRef = FirebaseStorage.getInstance().reference.child("District Images")
        districtRef = FirebaseDatabase.getInstance().reference.child("district")

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
                setCity(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }

        save_city?.setOnClickListener {
            description = districtDescription.text.toString()
            divisionName = division_name?.selectedItem.toString()
            districtName = city_name?.selectedItem.toString()
            if (cropImageUri == null || districtName == "Select District" || divisionName == "Select Division" || description == null) {
                when {
                    cropImageUri == null -> {
                        city_image?.background?.setColorFilter(
                                    resources.getColor(R.color.red),
                                    PorterDuff.Mode.SRC_ATOP)
                        Toast.makeText(
                            applicationContext,
                            "Please give city image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    description!!.isEmpty() -> {
                        description_layout.error = "Enter district description"
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
                    districtName == "Select District" -> {
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

    private fun uploadImage(loading: ProgressDialog?) {
        if (cropImageUri != null) {
            val ref = districtImageRef?.child("uploads/" + UUID.randomUUID().toString())
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

    private fun setCity(position: Int) {
        val barisal = arrayOf(
            "Select District",
            "Barisal",
            "Barguna",
            "Bhola",
            "Jhalokati",
            "Patuakhali",
            "Pirojpur"
        )
        val chittagong = arrayOf(
            "Select District",
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
            "Select District",
            "Dhaka",
            "Faridpur",
            "Gazipur",
            "Gopalganj",
            "Kishoreganj",
            "Madaripur",
            "Manikganj",
            "Munshiganj",
            "Narayanganj",
            "Narsingdi",
            "Rajbari",
            "Shariatpur",
            "Tangail"
        )
        val khulna = arrayOf(
            "Select District",
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
        val mymensingh = arrayOf("Select District", "Jamalpur", "Mymensingh", "Netrokona", "Sherpur")
        val rajshahi = arrayOf(
            "Select District",
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
            "Select District",
            "Dinajpur",
            "Gaibandha",
            "Kurigram",
            "Lalmonirhat",
            "Nilphamari",
            "Panchagarh",
            "Rangpur",
            "Thakurgaon"
        )
        val sylhet = arrayOf("Select District", "Habiganj", "Moulvibazar", "Sunamganj", "Sylhet")

        when (position) {
            1 -> {
                val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, barisal)
                city_name.adapter = arrayAdapter
            }
            2 -> {
                val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, chittagong)
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
                val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mymensingh)
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

    private fun addUploadRecordToDb(uri: String, loading: ProgressDialog?) {
        val districtKey = districtRef?.push()?.key
        val data = HashMap<String, Any>()
        data["imageUrl"] = uri
        data["districtName"] = districtName.toString()
        data["description"] = description.toString()
        data["division"] = divisionName.toString()
        data["districtKey"] = districtKey.toString()

        districtRef?.child(districtKey!!)?.setValue(data)?.addOnSuccessListener {
            loading?.dismiss()
            toasty(this, "Created new city successfully")
            val intent = Intent(applicationContext, AdminPanelActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }?.addOnFailureListener {
            toasty(this, "Error saving to DB")
            loading?.dismiss()
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
                city_image.setImageURI(result.uri)
                cropImageUri = result.uri
                city_image?.background?.setColorFilter(
                    resources.getColor(R.color.edittext_border),
                    PorterDuff.Mode.SRC_ATOP
                )
                city_image?.background?.setColorFilter(
                    resources.getColor(R.color.edittext_border),
                    PorterDuff.Mode.SRC_ATOP)
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

    private fun successfulDialog(activity: Activity, _title: String, _desc: String) {
        flag = 1
        val dialog = Dialog(activity)
        dialog.setContentView(R.layout.successful_dialog)

        val title = dialog.findViewById<TextView>(R.id.title)
        val desc = dialog.findViewById<TextView>(R.id.desc)
        val ok = dialog.findViewById<TextView>(R.id.ok)

        ok.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(applicationContext, AdminPanelActivity::class.java)
            startActivity(intent)
            finish()
        }

        title.text = _title
        desc.text = _desc

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.show()
    }

    override fun onBackPressed() {
        if (flag != 1) {
            super.onBackPressed()
        }
    }
}