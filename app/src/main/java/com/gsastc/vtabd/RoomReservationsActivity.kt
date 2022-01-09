package com.gsastc.vtabd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.gsastc.vtabd.adapter.ReservationsAdapter
import com.gsastc.vtabd.model.DataModel
import kotlinx.android.synthetic.main.activity_reservations.*
import java.util.*

class RoomReservationsActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var reservationAdapter: ReservationsAdapter? = null
    private var dataList: MutableList<DataModel>? = null

    lateinit var mAuth: FirebaseAuth
    var currentUserID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservations)

        val mToolbar = findViewById<MaterialToolbar>(R.id.reservation_toolbar)
        setSupportActionBar(mToolbar)
        Objects.requireNonNull(supportActionBar)?.setDisplayHomeAsUpEnabled(true)
        val toolbarTv = findViewById<TextView>(R.id.reservation_tv)
        toolbarTv.text = "Reservations"

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.currentUser?.uid

        shimmerFrameLayout = findViewById(R.id.reservation_shimmer_view_container)

        recyclerView = findViewById(R.id.reservation_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        dataList = ArrayList()
        retrieveReservation()
    }

    private fun retrieveReservation() {
        val orderRef = FirebaseDatabase.getInstance().reference.child("Hotel").child(currentUserID!!).child("reservations")
        orderRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val orders = snapshot.getValue(DataModel::class.java)
                    if (orders != null) {
                        dataList?.add(orders)
                        reservationAdapter = ReservationsAdapter(this@RoomReservationsActivity, dataList as ArrayList<DataModel>)
                        recyclerView?.adapter = reservationAdapter
                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                        recyclerView?.visibility = View.VISIBLE
                        empty_reservation?.visibility = View.GONE
                    }
                }
                reservationAdapter?.notifyDataSetChanged()

                if (dataList?.size == 0) {
                    shimmerFrameLayout?.stopShimmer()
                    empty_reservation?.visibility = View.VISIBLE
                    recyclerView?.visibility = View.GONE
                    shimmerFrameLayout?.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}