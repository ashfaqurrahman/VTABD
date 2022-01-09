package com.gsastc.vtabd.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.gsastc.vtabd.R
import com.gsastc.vtabd.adapter.BlogAdapter
import com.gsastc.vtabd.model.DataModel
import kotlinx.android.synthetic.main.fragment_blog.*

class BlogFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var blogAdapter: BlogAdapter? = null
    private var dataList: MutableList<DataModel>? = null
    private lateinit var mAuth: FirebaseAuth
    private var currentUserID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_blog, container, false)

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.currentUser?.uid
        shimmerFrameLayout = view.findViewById(R.id.blog_shimmer_view_container) as ShimmerFrameLayout
        recyclerView = view.findViewById(R.id.blog_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        dataList = ArrayList()
        retrieveBlog()

        return view
    }

    private fun retrieveBlog() {
        val blogRef = FirebaseDatabase.getInstance().reference.child("blog")
        val query: Query = blogRef.orderByChild("visibility").equalTo("Show")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val model = snapshot.getValue(DataModel::class.java)
                    if (model != null) {
                        dataList?.add(model)
                        blogAdapter = context?.let { BlogAdapter(it, dataList as ArrayList<DataModel>) }
                        recyclerView?.adapter = blogAdapter
                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                        recyclerView?.visibility = View.VISIBLE
                        empty_blog?.visibility = View.GONE
                    }
                }
                blogAdapter?.notifyDataSetChanged()
                if (dataList?.size == 0) {
                    shimmerFrameLayout?.stopShimmer()
                    shimmerFrameLayout?.visibility = View.GONE
                    recyclerView?.visibility = View.GONE
                    empty_blog?.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onResume() {
        shimmerFrameLayout?.startShimmer()
        super.onResume()
    }

//    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
//        super.setUserVisibleHint(isVisibleToUser)
//        if (isVisibleToUser) {
//            requireFragmentManager().beginTransaction().detach(this).attach(this).commit()
//        }
//    }

}