package com.gsastc.vtabd

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.database.*
import com.gsastc.vtabd.R.string
import com.gsastc.vtabd.adapter.AcReOrderAdapter
import com.gsastc.vtabd.adapter.PendingOrderAdapter
import com.gsastc.vtabd.model.DataModel
import com.gsastc.vtabd.utils.logoutUser
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout
import com.mikepenz.materialdrawer.AccountHeaderBuilder
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.interfaces.ICrossfader
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.ProfileDrawerItem
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.IProfile
import com.mikepenz.materialdrawer.util.DrawerUIUtils
import com.mikepenz.materialize.util.UIUtils
import kotlinx.android.synthetic.main.content_main.*

class AdminPanelActivity : AppCompatActivity() {
    private var result: Drawer? = null
    private var crossfadeDrawerLayout: CrossfadeDrawerLayout? = null

    private var shimmerFrameLayout: ShimmerFrameLayout? = null
    private var pendingRecyclerView: RecyclerView? = null
    private var pendingOrderAdapter: PendingOrderAdapter? = null
    private var pendingOrder: MutableList<DataModel>? = null
    private var acReRecyclerView: RecyclerView? = null
    private var acReOrderAdapter: AcReOrderAdapter? = null
    private var acReOrder: MutableList<DataModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_panel)

        val typeface = ResourcesCompat.getFont(this, R.font.blacklist)
        val appName = findViewById<TextView>(R.id.appname)
        appName.typeface = typeface

        inflateNavDrawer()

        shimmerFrameLayout = findViewById(R.id.order_shimmer_view_container)
        pendingRecyclerView = findViewById(R.id.pending_order_list)
        pendingRecyclerView?.setHasFixedSize(true)
        pendingRecyclerView?.layoutManager = LinearLayoutManager(this)
        pendingOrder = ArrayList()
        retrievePendingOrder()
        acReRecyclerView = findViewById(R.id.ac_re_order_list)
        acReRecyclerView?.setHasFixedSize(true)
        acReRecyclerView?.layoutManager = LinearLayoutManager(this)
        acReOrder = ArrayList()
        retrieveAcReOrder()
    }

    private fun retrievePendingOrder() {
        val orderRef = FirebaseDatabase.getInstance().reference.child("Order")
        val query: Query = orderRef.orderByChild("status").equalTo("Pending")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                pendingOrder?.clear()
                for (snapshot in dataSnapshot.children) {
                    val dataModel = snapshot.getValue(DataModel::class.java)
                    if (dataModel != null) {
                        pendingOrder?.add(dataModel)

                        pendingOrderAdapter = this@AdminPanelActivity?.let {
                            PendingOrderAdapter(
                                it,
                                pendingOrder as ArrayList<DataModel>
                            )
                        }
                        pendingRecyclerView?.adapter = pendingOrderAdapter
                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                        order_layout?.visibility = View.VISIBLE
                    }
                }
                pendingOrderAdapter?.notifyDataSetChanged()

                if (pendingOrder?.size == 0) {
                    shimmerFrameLayout?.stopShimmer()
                    shimmerFrameLayout?.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun retrieveAcReOrder() {
        val orderRef = FirebaseDatabase.getInstance().reference.child("Order")
        val query: Query = orderRef.orderByChild("slag").equalTo("AcceptedRejected")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                acReOrder?.clear()
                for (snapshot in dataSnapshot.children) {
                    val dataModel = snapshot.getValue(DataModel::class.java)
                    if (dataModel != null) {
                        acReOrder?.add(dataModel)

                        acReOrderAdapter = this@AdminPanelActivity?.let {
                            AcReOrderAdapter(
                                it,
                                acReOrder as ArrayList<DataModel>
                            )
                        }
                        acReRecyclerView?.adapter = acReOrderAdapter
                        shimmerFrameLayout?.stopShimmer()
                        shimmerFrameLayout?.visibility = View.GONE
                        order_layout?.visibility = View.VISIBLE
                    }
                }
                acReOrderAdapter?.notifyDataSetChanged()

                if (acReOrder?.size == 0) {
                    shimmerFrameLayout?.stopShimmer()
                    shimmerFrameLayout?.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun inflateNavDrawer() {

        //set Custom toolbar to activity -----------------------------------------------------------
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Create the AccountHeader ----------------------------------------------------------------
        //masum
        //Profile Making
        val profile: IProfile<*> = ProfileDrawerItem()
            .withName("Admin Panel")
            .withEmail("+8801755467748")
            .withIcon(resources.getDrawable(R.drawable.cholo_ghuri))
        val headerResult = AccountHeaderBuilder()
            .withActivity(this)
            .withHeaderBackground(R.color.gradient_background)
            .addProfiles(profile)
            .withCompactStyle(true)
            .build()

        //Adding nav drawer items ------------------------------------------------------------------
        val item1 = PrimaryDrawerItem().withIdentifier(1).withName(string.home)
            .withIcon(R.drawable.ic_baseline_home_24)
        val item2 = PrimaryDrawerItem().withIdentifier(2).withName(string.create_new_place)
            .withIcon(R.drawable.ic_add_location)
        val item3 = PrimaryDrawerItem().withIdentifier(3).withName("Create District")
            .withIcon(R.drawable.ic_city)
        val item4 = PrimaryDrawerItem().withIdentifier(4).withName(string.logout)
            .withIcon(R.drawable.ic_log_out)
        val item5 = SecondaryDrawerItem().withIdentifier(5).withName("System Users")
            .withIcon(R.drawable.ic_user)
        val item6 =
            SecondaryDrawerItem().withIdentifier(6).withName("Blog").withIcon(R.drawable.ic_blog)
        val item7 = SecondaryDrawerItem().withIdentifier(7).withName("Rooms")
            .withIcon(R.drawable.ic_local_hotel)
        val item8 = SecondaryDrawerItem().withIdentifier(8).withName("Vehicles")
            .withIcon(R.drawable.transport)
        val item9 = SecondaryDrawerItem().withIdentifier(9).withName("District")
            .withIcon(R.drawable.location_city)
        val item10 = SecondaryDrawerItem().withIdentifier(10).withName("Place")
            .withIcon(R.drawable.ic_location_icon_solid)


        //creating navBar and adding to the toolbar ------------------------------------------------
        result = DrawerBuilder()
            .withActivity(this)
            .withToolbar(toolbar)
            .withHasStableIds(true)
            .withDrawerLayout(R.layout.crossfade_drawer)
            .withAccountHeader(headerResult)
            .withDrawerWidthDp(72)
            .withGenerateMiniDrawer(true)
            .withTranslucentStatusBar(true)
            .withActionBarDrawerToggleAnimated(true)
            .addDrawerItems(
                item1, item2, item3, item4, DividerDrawerItem(), item5, item6, item7, item8, item9, item10
            )
            .withOnDrawerItemClickListener { _, position, _ ->
                when (position) {
                    1 -> if (result != null && result!!.isDrawerOpen) {
                        result!!.closeDrawer()
                    }
                    2 -> startActivity(
                        Intent(
                            this@AdminPanelActivity,
                            CreatePlaceActivity::class.java
                        )
                    )
                    3 -> startActivity(
                        Intent(
                            this@AdminPanelActivity,
                            CreateDistrictActivity::class.java
                        )
                    )
                    4 -> logoutUser(this@AdminPanelActivity)
                    6 -> startActivity(
                        Intent(
                            this@AdminPanelActivity,
                            SystemUserActivity::class.java
                        )
                    )
                    7 -> startActivity(
                        Intent(
                            this@AdminPanelActivity,
                            SystemBlogActivity::class.java
                        )
                    )
                    8 -> startActivity(
                        Intent(
                            this@AdminPanelActivity,
                            SystemRoomActivity::class.java
                        )
                    )
                    9 -> startActivity(
                        Intent(
                            this@AdminPanelActivity,
                            SystemVehiclesActivity::class.java
                        )
                    )
                    10 -> startActivity(
                        Intent(
                            this@AdminPanelActivity,
                            SystemDistrictActivity::class.java
                        )
                    )
                    11 -> startActivity(
                        Intent(
                            this@AdminPanelActivity,
                            SystemPlaceActivity::class.java
                        )
                    )
                    else -> Toast.makeText(this@AdminPanelActivity, "Default", Toast.LENGTH_LONG)
                        .show()
                }
                true
            }
            .build()

        //Setting crossfader drawer------------------------------------------------------------
        crossfadeDrawerLayout = result!!.drawerLayout as CrossfadeDrawerLayout

        //define maxDrawerWidth
        crossfadeDrawerLayout!!.setMaxWidthPx(DrawerUIUtils.getOptimalDrawerWidth(this))

        //add second view (which is the miniDrawer)
        val miniResult = result!!.miniDrawer

        //build the view for the MiniDrawer
        val view = miniResult.build(this)

        //set the background of the MiniDrawer as this would be transparent
        //view.setBackgroundColor(UIUtils.getThemeColorFromAttrOrRes(this, com.mikepenz.materialdrawer.R.attr.material_drawer_background, com.mikepenz.materialdrawer.R.color.material_drawer_background));
        view.setBackgroundColor(
            UIUtils.getThemeColorFromAttrOrRes(
                this,
                R.attr.material_drawer_background,
                R.color.material_drawer_background
            )
        )

        //we do not have the MiniDrawer view during CrossfadeDrawerLayout creation so we will add it here
        crossfadeDrawerLayout!!.smallView.addView(
            view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        //define the crossfader to be used with the miniDrawer. This is required to be able to automatically toggle open / close
        miniResult.withCrossFader(object : ICrossfader {
            override fun crossfade() {
                val isFaded = isCrossfaded
                crossfadeDrawerLayout!!.crossfade(400)

                //only close the drawer if we were already faded and want to close it now
                if (isFaded) {
                    result!!.drawerLayout.closeDrawer(GravityCompat.START)
                }
            }

            override fun isCrossfaded(): Boolean {
                return crossfadeDrawerLayout!!.isCrossfaded
            }
        })

        //hook to the crossfade event
        crossfadeDrawerLayout!!.withCrossfadeListener { containerView, currentSlidePercentage, slideOffset ->
            //Log.e("CrossfadeDrawerLayout", "crossfade: " + currentSlidePercentage + " - " + slideOffset);
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        //add the values which need to be saved from the drawer to the bundle
        var outState: Bundle? = outState
        outState = result!!.saveInstanceState(outState)
        //add the values which need to be saved from the accountHeader to the bundle
        outState = result!!.saveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result!!.isDrawerOpen) {
            result!!.closeDrawer()
        } else {
            super.onBackPressed()
        }
    }
}