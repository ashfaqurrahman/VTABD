package com.gsastc.vtabd

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_blog_details.*

class BlogDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blog_details)

        setSupportActionBar(toolbar)
        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setHomeAsUpIndicator(resources.getDrawable(R.drawable.ic_baseline_arrow_back_24))
        toolbar!!.setNavigationOnClickListener {
            finish()
        }

        Picasso.get().load(intent.getStringExtra("imageUrl")).placeholder(R.drawable.empty)
            .into(blog_image)
        date?.text = intent.getStringExtra("date")
        time?.text = intent.getStringExtra("time")
        author?.text = intent.getStringExtra("authorName")
        blog_title?.text = intent.getStringExtra("blogName")
        blog_desc?.text = intent.getStringExtra("description")

        val collapsingToolbar = findViewById<CollapsingToolbarLayout>(R.id.collapsing_toolbar)

        val bitmap = (blog_image.drawable as BitmapDrawable).bitmap
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
                }
                // when scrollbar on top...Collapsed
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.title = intent.getStringExtra("blogName")
                    toolbar.setBackgroundColor(getDominantColor(bitmap))
                    collapsingToolbar.setCollapsedTitleTextColor(Color.BLACK)
                    isShow = true
                }
                // when scroll bar on down side...Expanded
                else if (isShow) {
                    collapsingToolbar.title = ""
                    toolbar.setBackgroundColor(Color.TRANSPARENT)
                    isShow = false
                }
            }
        })
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