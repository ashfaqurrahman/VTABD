package com.gsastc.vtabd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gsastc.vtabd.adapter.SystemBlogAdapter
import com.gsastc.vtabd.model.DataModel
import kotlinx.android.synthetic.main.activity_system_blog.*
import java.util.*
import kotlin.collections.ArrayList

class SystemBlogActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var blogAdapter: SystemBlogAdapter? = null
    private var blogDataList: MutableList<DataModel>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_blog)

        val mToolbar = findViewById<MaterialToolbar>(R.id.blog_toolbar)
        setSupportActionBar(mToolbar)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        val toolbarTv = findViewById<TextView>(R.id.blog_tv)
        toolbarTv.text = "Blog"

        shimmerFrameLayout = findViewById(R.id.blog_shimmer_view_container)

        recyclerView = findViewById(R.id.blog_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        blogDataList = ArrayList()
        retrieveBlog()
    }

    private fun retrieveBlog() {
        val blogRef = FirebaseDatabase.getInstance().reference.child("blog")
        blogRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                blogDataList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val model = snapshot.getValue(DataModel::class.java)
                    if (model != null) {
                        blogDataList?.add(model)
                        blogAdapter = SystemBlogAdapter(this@SystemBlogActivity, blogDataList as ArrayList<DataModel>)
                        recyclerView?.adapter = blogAdapter
                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                        blog_layout?.visibility = View.VISIBLE
                        empty_blog?.visibility = View.GONE
                    }
                }
                blogAdapter?.notifyDataSetChanged()

                if (blogDataList?.size == 0) {
                    shimmerFrameLayout?.stopShimmer()
                    shimmerFrameLayout?.visibility = View.GONE
                    empty_blog?.visibility = View.VISIBLE
                    blog_layout?.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}