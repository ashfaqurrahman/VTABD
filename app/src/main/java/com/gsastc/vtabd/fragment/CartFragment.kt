package com.gsastc.vtabd.fragment

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import com.gsastc.vtabd.R
import com.gsastc.vtabd.SharedPrefManager
import com.gsastc.vtabd.adapter.CartAdapter
import com.gsastc.vtabd.adapter.DatabaseHelper
import com.gsastc.vtabd.model.CartModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_cart.view.*
import java.util.HashMap

class CartFragment : Fragment() {
    var myDB: DatabaseHelper? = null
    var cartItemList: ArrayList<CartModel>? = null
    private var adapter: CartAdapter? = null
    private var orderRef: DatabaseReference? = null
    private var historyRef: DatabaseReference? = null
    private lateinit var mAuth: FirebaseAuth
    private var currentUserID: String? = ""
    private var flag =0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        requireActivity().bottom_navigation.menu.getItem(1).isChecked = true

        mAuth = FirebaseAuth.getInstance()
        currentUserID = mAuth.currentUser?.uid
        orderRef = FirebaseDatabase.getInstance().reference.child("Order")
        historyRef = FirebaseDatabase.getInstance().reference.child("users").child(currentUserID!!)

        myDB = DatabaseHelper(context)
        val data = myDB?.listContents
        cartItemList = ArrayList()

        if (data?.count == 0) {
            view.empty_cart?.visibility = View.VISIBLE
            view.cart_item_layout?.visibility = View.GONE
        } else {
            view.empty_cart?.visibility = View.GONE
            view.cart_item_layout?.visibility = View.VISIBLE
            cartItemList?.clear()
            while (data!!.moveToNext()) {
                cartItemList?.add(
                    CartModel(
                        data.getInt(0),
                        data.getString(3),
                        data.getString(4),
                        data.getString(5),
                        data.getString(6),
                        data.getString(7),
                        data.getString(8),
                    )
                )
            }
            adapter?.notifyDataSetChanged()
        }

        val fragment = CartFragment()
        val recyclerView: RecyclerView = view!!.findViewById(R.id.cart_item_list)
        val gridLayoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.layoutManager = gridLayoutManager
        adapter = CartAdapter(requireContext(), cartItemList!!)
        recyclerView.adapter = adapter

        view.place_order?.setOnClickListener {
            val loading = ProgressDialog.show(
                context,
                "",
                "Please wait...",
                false,
                false
            )
            loading?.show()
            placeOrder(loading, view)
        }

        return view
    }

    private fun placeOrder(loading: ProgressDialog, view: View) {
        var counter = 0
        val data = myDB?.listContents
        while (data!!.moveToNext()) {
            counter += 1
            val orderKey = orderRef?.push()?.key
            val userName = SharedPrefManager.getInstance(requireContext())?.getUserName
            val phone = SharedPrefManager.getInstance(requireContext())?.getPhone
            val address = SharedPrefManager.getInstance(requireContext())!!.getDistrict + ", " + SharedPrefManager.getInstance(requireContext())!!.getDivision
            val orderData = HashMap<String, Any>()
            orderData["orderKey"] = orderKey.toString()
            orderData["targetedUserID"] = data.getString(2)
            orderData["name"] = data.getString(3)
            orderData["type"] = data.getString(4)
            orderData["checkInCheckOut"] = data.getString(5)
            orderData["rantPerDay"] = data.getString(6).toString()
            orderData["days"] = data.getString(7)
            orderData["totalAmount"] = data.getString(8)
            orderData["status"] = "Pending"
            orderData["slag"] = "Pending"
            orderData["userName"] = userName.toString()
            orderData["phone"] = phone.toString()
            orderData["address"] = address
            orderData["currentUserID"] = currentUserID.toString()
            orderRef?.child(orderKey!!)?.setValue(orderData)?.addOnSuccessListener {
                historyRef?.child("history")!!.child(orderKey).setValue(orderData).addOnSuccessListener {
                    if (data.count == counter) {
                        myDB?.deleteData()
                        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottom_navigation)
                        navBar.getOrCreateBadge(R.id.cart).number = 0
                        loading.dismiss()
                        successfulDialog(requireContext(), "Place Order", "Place order successfully")
                    }
                }.addOnFailureListener {
                    loading.dismiss()
                    Toast.makeText(context, "Check Internet Connection", Toast.LENGTH_SHORT).show()
                }
            }?.addOnFailureListener {
                loading.dismiss()
                Toast.makeText(context, "Check Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadFragment(fragment: Fragment) { // load fragment
        val transaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.commit()
    }

    private fun loadFragmentToBack(fragment: Fragment) { // load fragment
        val transaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.frame_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun successfulDialog(context: Context, _title: String, _desc: String) {
        flag = 1
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.successful_dialog)

        val title = dialog.findViewById<TextView>(R.id.title)
        val desc = dialog.findViewById<TextView>(R.id.desc)
        val ok = dialog.findViewById<TextView>(R.id.ok)

        ok.setOnClickListener {
            dialog.dismiss()
            loadFragment(HomeFragment())
        }

        title.text = _title
        desc.text = _desc

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setCancelable(false)
        dialog.show()
    }
}
