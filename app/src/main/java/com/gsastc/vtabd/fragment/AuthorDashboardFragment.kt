package com.gsastc.vtabd.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.gsastc.vtabd.R
import com.gsastc.vtabd.WriteBlogActivity
import com.gsastc.vtabd.adapter.SystemBlogAdapter
import com.gsastc.vtabd.model.DataModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_author_dashboard.view.*
import java.util.*

class AuthorDashboardFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var blogAdapter: SystemBlogAdapter? = null
    private var dataList: MutableList<DataModel>? = null
    private lateinit var mAuth: FirebaseAuth
    private var currentUserID: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_author_dashboard, container, false)

        val typeface = ResourcesCompat.getFont(requireContext(), R.font.blacklist)
        val appName = view.findViewById<TextView>(R.id.appname)
        appName.typeface = typeface

        requireActivity().bottom_navigation.menu.getItem(0).isChecked = true

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.currentUser?.uid
        shimmerFrameLayout = view.findViewById(R.id.blog_shimmer_view_container)
        recyclerView = view.findViewById(R.id.blog_list)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)
        dataList = ArrayList()
        retrieveMyBlog()

        view?.author_fab?.setButtonIconResource(R.drawable.ic_baseline_note_add_24)
        view?.author_fab?.setOnClickListener {
            startActivity(Intent(context, WriteBlogActivity::class.java) )
        }

        return view
    }

    private fun retrieveMyBlog() {
        val orderRef = FirebaseDatabase.getInstance().reference.child("blog")
        val query: Query = orderRef.orderByChild("authorID").equalTo(currentUserID)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataList?.clear()
                for (snapshot in dataSnapshot.children) {
                    val model = snapshot.getValue(DataModel::class.java)
                    if (model != null) {
                        dataList?.add(model)
                        blogAdapter = SystemBlogAdapter(
                            context!!,
                            dataList as ArrayList<DataModel>
                        )
                        recyclerView?.adapter = blogAdapter
                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                        view?.blog_layout?.visibility = View.VISIBLE
                        view?.empty_blog_list?.visibility = View.GONE
                    }
                }
                blogAdapter?.notifyDataSetChanged()

                if (dataList?.size == 0) {
                    shimmerFrameLayout?.stopShimmer()
                    shimmerFrameLayout?.visibility = View.GONE
                    view?.blog_layout?.visibility = View.VISIBLE
                    view?.empty_blog_list?.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}