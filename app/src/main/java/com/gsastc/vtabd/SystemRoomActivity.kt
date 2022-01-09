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
import com.gsastc.vtabd.adapter.SystemRoomAdapter
import com.gsastc.vtabd.model.DataModel
import kotlinx.android.synthetic.main.activity_system_room.*
import java.util.*

class SystemRoomActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var rooAdapter: SystemRoomAdapter? = null
    private var mRooms: MutableList<DataModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_system_room)

        val mToolbar = findViewById<MaterialToolbar>(R.id.room_toolbar)
        setSupportActionBar(mToolbar)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        val toolbarTv = findViewById<TextView>(R.id.room_tv)
        toolbarTv.text = "Rooms"

        shimmerFrameLayout = findViewById(R.id.room_shimmer_view_container)
        recyclerView = findViewById(R.id.room_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        mRooms = ArrayList()
        retrieveRooms()
    }

    private fun retrieveRooms() {
        val roomRef = FirebaseDatabase.getInstance().reference.child("Rooms")
        roomRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mRooms?.clear()
                for (snapshot in dataSnapshot.children) {
                    val rooms = snapshot.getValue(DataModel::class.java)
                    if (rooms != null) {
                        mRooms?.add(rooms)
                        rooAdapter = SystemRoomAdapter(this@SystemRoomActivity, mRooms as ArrayList<DataModel>)
                        recyclerView?.adapter = rooAdapter
                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                        room_layout?.visibility = View.VISIBLE
                        empty_rooms?.visibility = View.GONE
                    }
                }
                rooAdapter?.notifyDataSetChanged()
                if (mRooms?.size == 0) {
                    shimmerFrameLayout?.stopShimmer()
                    empty_rooms?.visibility = View.VISIBLE
                    room_layout?.visibility = View.GONE
                    shimmerFrameLayout?.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}