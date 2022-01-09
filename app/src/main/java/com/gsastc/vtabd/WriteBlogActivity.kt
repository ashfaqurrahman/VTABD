package com.gsastc.vtabd

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.gsastc.vtabd.model.AuthorModel
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_write_blog.*
import kotlinx.android.synthetic.main.fragment_author_profile.view.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class WriteBlogActivity : AppCompatActivity() {
    private var cropImageUri: Uri? = null
    private var blogImageRef: StorageReference? = null
    private var blogRef: DatabaseReference? = null

    private var divisionName: String? = null
    private var cityName: String? = null
    private var blogName: String? = null
    private var desc: String? = null

    private var currentUserID: String? = null
    private lateinit var mAuth: FirebaseAuth
    private lateinit var date: String
    private lateinit var time: String
    var authorName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_blog)

        close_blog?.setOnClickListener {
            finish()
        }

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.currentUser?.uid
        blogImageRef = FirebaseStorage.getInstance().reference.child("blog images")
        blogRef = FirebaseDatabase.getInstance().reference.child("blog")

        val guideRef = FirebaseDatabase.getInstance().reference.child("author").child(currentUserID!!)
        val myData = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val data = dataSnapshot.getValue(AuthorModel::class.java)
                if (data?.authorName != null) {
                    authorName = data.authorName
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(
                    this@WriteBlogActivity,
                    "Please give your Bio",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        guideRef.addValueEventListener(myData)

        blog_image?.setOnClickListener {
            CropImage.startPickImageActivity(this)
        }

        val sdf = SimpleDateFormat("yyyy.MMM.dd", Locale.getDefault())
        val calendarDate = Calendar.getInstance()
        date = sdf.format(calendarDate.time)

        val stf = SimpleDateFormat("EEEE, hh:mm aa", Locale.getDefault())
        val calendarTime = Calendar.getInstance()
        time = stf.format(calendarTime.time)


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
                saveBlog(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Code to perform some action when nothing is selected
            }
        }

        save_blog?.setOnClickListener {
            blogName = blog_name.text.toString()
            desc = description.text.toString()
            divisionName = division_name.selectedItem.toString()
            cityName = city_name?.selectedItem.toString()
            if (blogName!!.isEmpty() || desc!!.isEmpty() || cityName == "Select City" || divisionName == "Select Division" || cropImageUri == null) {
                when {
                    cropImageUri == null -> {
                        blog_image?.background?.setColorFilter(
                            resources.getColor(R.color.red),
                            PorterDuff.Mode.SRC_ATOP
                        )
                        Toast.makeText(
                            applicationContext,
                            "Please give blog image",
                            Toast.LENGTH_SHORT
                        ).show();
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
                        ).show();
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
                        ).show();
                    }
                    blogName!!.isEmpty() -> {
                        blog_name?.background?.setColorFilter(
                            resources.getColor(R.color.red),
                            PorterDuff.Mode.SRC_ATOP
                        )

                        Toast.makeText(
                            applicationContext,
                            "Please give blog name",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    desc!!.isEmpty() -> {
                        description?.background?.setColorFilter(
                            resources.getColor(R.color.red),
                            PorterDuff.Mode.SRC_ATOP
                        )
                        Toast.makeText(
                            applicationContext,
                            "Please give blog description",
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

        blog_name.addTextChangedListener(nameTextWatcher)
        description.addTextChangedListener(descTextWatcher)
    }

    private val nameTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            if (s!!.isEmpty()) {
                blog_name.background.setColorFilter(
                    resources.getColor(R.color.red),
                    PorterDuff.Mode.SRC_ATOP
                )
            } else {
                blog_name.background.setColorFilter(
                    resources.getColor(R.color.edittext_border),
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private val descTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(
            s: CharSequence?,
            start: Int,
            before: Int,
            count: Int
        ) {
            if (description.text.toString().isEmpty()) {
                description.background.setColorFilter(
                    resources.getColor(R.color.red),
                    PorterDuff.Mode.SRC_ATOP
                )
            } else {
                description.background.setColorFilter(
                    resources.getColor(R.color.edittext_border),
                    PorterDuff.Mode.SRC_ATOP
                )
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }


    private fun saveBlog(position: Int) {
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
                val arrayAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    chittagong
                )
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
                val arrayAdapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    mymensingh
                )
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

    private fun uploadImage(loading: ProgressDialog) {
        if (cropImageUri != null) {
            val ref = blogImageRef?.child("uploads/" + UUID.randomUUID().toString())
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
        val blogKey = blogRef?.push()?.key
        val data = HashMap<String, Any>()
        data["blogKey"] = blogKey.toString()
        data["authorName"] = authorName.toString()
        data["authorID"] = currentUserID.toString()
        data["division"] = divisionName.toString()
        data["city"] = cityName.toString()
        data["date"] = date
        data["time"] = time
        data["imageUrl"] = uri
        data["blogName"] = blogName.toString()
        data["description"] = desc.toString()
        data["visibility"] = "Show"

        blogRef?.child(blogKey!!)?.setValue(data)?.addOnSuccessListener {
            loading.dismiss()
            successfulDialog(this, "Created Blog", "Create new blog successfully")
        }?.addOnFailureListener {
            Toast.makeText(this, "Error saving to DB", Toast.LENGTH_LONG).show()
            loading.dismiss()
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
                blog_image.setImageURI(result.uri)
                cropImageUri = result.uri
                blog_image?.background?.setColorFilter(
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
                Toast.LENGTH_SHORT
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

    private fun successfulDialog(context: Context, _title: String, _desc: String) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.successful_dialog)

        val title = dialog.findViewById<TextView>(R.id.title)
        val desc = dialog.findViewById<TextView>(R.id.desc)
        val ok = dialog.findViewById<TextView>(R.id.ok)

        ok.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(applicationContext, AuthorActivity::class.java)
            startActivity(intent)
            finish()
        }

        title.text = _title
        desc.text = _desc

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.setCancelable(false)
        dialog.show()
    }
}