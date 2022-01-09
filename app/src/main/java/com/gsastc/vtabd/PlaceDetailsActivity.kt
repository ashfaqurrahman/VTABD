package com.gsastc.vtabd

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_place_details.*
import uk.co.markormesher.android_fab.SpeedDialMenuAdapter
import uk.co.markormesher.android_fab.SpeedDialMenuItem


@Suppress("DEPRECATION")
class PlaceDetailsActivity : AppCompatActivity() {

    private var activeToast: Toast? = null
    private var clickCounter = 0
    private var buttonIcon = 0
    private var speedDialSize = 0
    private lateinit var city: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_details)

        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeAsUpIndicator(resources.getDrawable(R.drawable.ic_baseline_arrow_back_24))
        toolbar!!.setNavigationOnClickListener {
            finish()
        }

        city = intent.getStringExtra("city")!!

        val mAuth = FirebaseAuth.getInstance()
        val currentUserID = mAuth.currentUser?.uid
        if (currentUserID == "dyQK5ePVnJVYOF85bcbEX27XUTI2") {
            fab.visibility = View.GONE
        }
        fab.setOnClickListener { toast(getString(R.string.toast_click, ++clickCounter)) }
        fab.speedDialMenuAdapter = speedDialMenuAdapter
        fab.contentCoverEnabled = true
        fab.setButtonPosition(
            uk.co.markormesher.android_fab.FloatingActionButton.POSITION_BOTTOM.or(
                uk.co.markormesher.android_fab.FloatingActionButton.POSITION_END
            )
        )
        //fab.setBackgroundColor(0xff0099ff.toInt())
        fab.setButtonIconResource(R.drawable.ic_add)
        //fab.setContentCoverColour(0xcceeffff.toInt())
        fab.show()

        Picasso.get().load(intent.getStringExtra("imageUrl")).placeholder(R.drawable.empty)
            .into(place_image)
        place_desc.text = intent.getStringExtra("desc")
        place_district.text = intent.getStringExtra("city") + ", " + intent.getStringExtra("division")

        val collapsingToolbar = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)

        val bitmap = (place_image.drawable as BitmapDrawable).bitmap
        window.statusBarColor = getDominantColor(bitmap)
        toolbar.setTitleTextColor(getDominantContrasColor(bitmap))

        val mAppBarLayout = findViewById<View>(R.id.app_bar_layout) as AppBarLayout
        mAppBarLayout.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = false
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                // when create page first time...
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                    collapsingToolbar.title = intent.getStringExtra("placeName")
                    collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.white))
                }
                // when scrollbar on top...Collapsed
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.title = intent.getStringExtra("placeName")
                    toolbar.setBackgroundColor(getDominantColor(bitmap))
                    collapsingToolbar.setCollapsedTitleTextColor(Color.BLACK)
                    place_district.visibility = View.GONE
                    isShow = true
                }
                // when scroll bar on down side...Expanded
                else if (isShow) {
                    collapsingToolbar.title = intent.getStringExtra("placeName")
                    toolbar.setBackgroundColor(Color.TRANSPARENT)
                    collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.white))
                    place_district.visibility = View.VISIBLE
                    isShow = false
                }
            }
        })
    }

    private val speedDialMenuAdapter = object: SpeedDialMenuAdapter() {

        override fun getCount(): Int = 3

        override fun getMenuItem(context: Context, position: Int): SpeedDialMenuItem = when (position) {
            0 -> SpeedDialMenuItem(context, R.drawable.ic_baseline_hotel_24, "Room Booking")
            1 -> SpeedDialMenuItem(context, R.drawable.ic_baseline_local_taxi_24, "Vehicle Hire")
            2 -> SpeedDialMenuItem(context, R.drawable.ic_baseline_person_outline_24, "Guide Hire")
            else -> throw IllegalArgumentException("No menu item: $position")
        }

        override fun onMenuItemClick(position: Int): Boolean {
            when(position) {
                0 -> {
                    val intent = Intent(this@PlaceDetailsActivity, DealWithResortActivity::class.java)
                    intent.putExtra("city", city)
                    intent.putExtra("hire", "hotel")
                    startActivity(intent)
                }

                1 -> {
                    val intent = Intent(this@PlaceDetailsActivity, DealWithResortActivity::class.java)
                    intent.putExtra("city", city)
                    intent.putExtra("hire", "vehicle")
                    startActivity(intent)
                }

                2 -> {
                    val intent = Intent(this@PlaceDetailsActivity, DealWithResortActivity::class.java)
                    intent.putExtra("city", city)
                    intent.putExtra("hire", "guide")
                    startActivity(intent)
                }

            }
            return true
        }

        override fun onPrepareItemLabel(context: Context, position: Int, label: TextView) {
            // make the first item bold if there are multiple items
            // (this isn't a design pattern, it's just to demo the functionality)
            if (position == 0 && speedDialSize > 1) {
                label.setTypeface(label.typeface, Typeface.BOLD)
            }
        }

        // rotate the "+" icon only
        override fun fabRotationDegrees(): Float = if (buttonIcon == 0) 135F else 0F
    }

    private fun toast(str: String) {
        activeToast?.cancel()
        activeToast = Toast.makeText(this, str, Toast.LENGTH_SHORT)
        activeToast?.show()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.main_menu, menu)
//        return true
//    }

    private fun getDominantColor(bitmap: Bitmap): Int {
        val bitMap = Bitmap.createScaledBitmap(bitmap, 1, 1, true)
        val color = bitMap.getPixel(0, 0)
        bitMap.recycle()
        return color
    }

    private fun getDominantContrasColor(bitmap: Bitmap): Int {
        val bitMap = Bitmap.createScaledBitmap(bitmap, 1, 1, true)
        val color = bitMap.getPixel(0, 0)
        bitMap.recycle()
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.rgb(255 - red, 255 - green, 255 - blue)
    }
}