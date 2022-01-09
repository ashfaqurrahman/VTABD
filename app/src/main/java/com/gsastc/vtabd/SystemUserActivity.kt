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
import com.gsastc.vtabd.adapter.SystemUserAdapter
import com.gsastc.vtabd.model.DataModel
import java.util.*

class SystemUserActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var userAdapter: SystemUserAdapter? = null
    private var mUser: MutableList<DataModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_user)

        val mToolbar = findViewById<MaterialToolbar>(R.id.user_toolbar)
        setSupportActionBar(mToolbar)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        val toolbarTv = findViewById<TextView>(R.id.user_tv)
        toolbarTv.text = "System Users"

        shimmerFrameLayout = findViewById(R.id.user_shimmer_view_container)
        recyclerView = findViewById(R.id.user_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        mUser = ArrayList()
        retrieveUsers()
    }

    private fun retrieveUsers() {
        val guideRef = FirebaseDatabase.getInstance().reference.child("people")
        guideRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUser?.clear()
                for (snapshot in dataSnapshot.children) {
                    val dataModel = snapshot.getValue(DataModel::class.java)
                    if (dataModel != null) {
                        mUser?.add(dataModel)

                        userAdapter = SystemUserAdapter(this@SystemUserActivity, mUser as ArrayList<DataModel>)
                        recyclerView?.adapter = userAdapter

                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                    }
                }
                userAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}