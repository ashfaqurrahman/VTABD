package com.gsastc.vtabd

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import com.gsastc.vtabd.adapter.DatabaseHelper
import com.gsastc.vtabd.utils.toasty
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_vehicle_details.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

class VehicleDetailsActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private var day = 0
    private var month: Int = 0
    private var year: Int = 0
    private var status: Int? = null
    private var dateDifference: Long? = null
    private var df: SimpleDateFormat? = null
    
    @ExperimentalTime
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vehicle_details)

        back?.setOnClickListener {
            finish()
        }

        go_to_cart?.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("flag", 3)
            startActivity(intent)
        }

        val calendar = Calendar.getInstance()
        df = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate: String = df!!.format(calendar.time)
        check_in_date?.text = currentDate

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val nextDate: String = df!!.format(calendar.time)
        check_out_date?.text = nextDate

        checkValidaDate()

        Picasso.get().load(intent.getStringExtra("imageUrl")).placeholder(R.drawable.empty)
            .into(vehicle_image)
        toolbar_transport_name?.text = intent.getStringExtra("transportName")
        vehicle_name?.text = intent.getStringExtra("vehicleModelNane")
        address?.text = intent.getStringExtra("cityName")
        desc?.text = intent.getStringExtra("description")
        rant?.text = intent.getStringExtra("price")
        ac?.text = intent.getStringExtra("ac")
        capacity?.text = intent.getStringExtra("capacity")

        check_in?.setOnClickListener {
            status = 0
            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            val checkInDate = DatePickerDialog(
                this@VehicleDetailsActivity,
                this@VehicleDetailsActivity,
                year,
                month,
                day
            )
            checkInDate.show()
        }

        check_out?.setOnClickListener {
            status = 1
            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            val checkOutDate = DatePickerDialog(
                this@VehicleDetailsActivity,
                this@VehicleDetailsActivity,
                year,
                month,
                day
            )
            checkOutDate.show()
        }

        cart?.setOnClickListener {
            addToCart(dateDifference!!+1)
        }
    }

    @ExperimentalTime
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        when (status) {
            0 -> {
                check_in_date?.text = dayOfMonth.toString() + "/" + (month + 1) + "/" + year
            }
            1 -> {
                check_out_date?.text = dayOfMonth.toString() + "/" + (month + 1) + "/" + year
            }
        }
        checkValidaDate()
    }

    @ExperimentalTime
    private fun checkValidaDate() {
        dateDifference = getDateDiff(df!!, check_in_date.text.toString(), check_out_date.text.toString())
        if (dateDifference!!<0) {
            days?.text = "Invalid date range...!"
            days?.setTextColor(resources.getColor(R.color.red))
            cart?.visibility = View.GONE
        }
        else {
            days?.text = "For " + (dateDifference!! +1).toString() + " days"
            days?.setTextColor(resources.getColor(R.color.black))
            cart?.visibility = View.VISIBLE
        }
    }

    @ExperimentalTime
    fun getDateDiff(format: SimpleDateFormat, oldDate: String, newDate: String): Long {
        return try {
            DurationUnit.DAYS.convert(
                format.parse(newDate).time - format.parse(oldDate).time,
                DurationUnit.MILLISECONDS
            )
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    private fun addToCart(dateDiff: Long) {
        val myDB = DatabaseHelper(this)
        myDB.addData(
            intent.getStringExtra("vehicleKey"),
            intent.getStringExtra("transportID"),
            intent.getStringExtra("vehicleModelNane"),
            "Vehicle",
            check_in_date.text.toString() + " - " + check_out_date.text.toString(),
            intent.getStringExtra("price"),
            dateDiff.toString(),
            (intent.getStringExtra("price")!!.toInt() * dateDiff).toString()
        )
        toasty(this, "Add to cart successfully")
    }

}