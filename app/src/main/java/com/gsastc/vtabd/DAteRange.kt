package com.gsastc.vtabd

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.archit.calendardaterangepicker.customviews.CalendarListener
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView
import kotlinx.android.synthetic.main.activity_d_ate_range.*
import java.text.SimpleDateFormat
import java.util.*

class DAteRange : AppCompatActivity() {
    private var calendar: DateRangeCalendarView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_d_ate_range)

        calendar = findViewById(R.id.cdrvCalendar)
        val typeface = Typeface.createFromAsset(assets, "JosefinSans-Regular.ttf")
        calendar!!.setFonts(typeface)
        calendar!!.setCalendarListener(calendarListener)
        findViewById<View>(R.id.btnReset).setOnClickListener {
            calendar!!.resetAllSelectedViews()
            check_in.text = "Check in"
            check_out.text = "Check out"
        }

//        calendar.setNavLeftImage(ContextCompat.getDrawable(this,R.drawable.ic_left));
//        calendar.setNavRightImage(ContextCompat.getDrawable(this,R.drawable.ic_right));
        val startMonth = Calendar.getInstance()
        startMonth[startMonth[Calendar.YEAR], startMonth[Calendar.MARCH]] = startMonth[Calendar.DATE]
        val endMonth = startMonth.clone() as Calendar
        endMonth.add(Calendar.MONTH, Calendar.MONTH + 3)
        Log.d(
            TAG,
            "Start month: " + startMonth.time.toString() + " :: End month: " + endMonth.time.toString()
        )
        calendar!!.setVisibleMonthRange(startMonth, endMonth)
        calendar!!.setSelectableDateRange(startMonth, endMonth)

        val startDateSelectable = startMonth.clone() as Calendar
        startDateSelectable.add(Calendar.DATE, 0)
        val endDateSelectable = endMonth.clone() as Calendar
        endDateSelectable.add(Calendar.DATE, 0)
        calendar!!.setSelectableDateRange(startDateSelectable, endDateSelectable)

        val startSelectedDate = startDateSelectable.clone() as Calendar
        startSelectedDate.add(Calendar.DATE, 0)
        val endSelectedDate = endDateSelectable.clone() as Calendar
        endSelectedDate.add(Calendar.DATE, 0)
        calendar!!.setSelectedDateRange(startSelectedDate, endSelectedDate)

        /*val current = startMonth.clone() as Calendar
        current.add(Calendar.MONTH, 1)
        calendar!!.setCurrentMonth(current)*/

        /*final Calendar startDateSelectable = (Calendar) startMonth.clone();
        startDateSelectable.add(Calendar.DATE, 20);
        final Calendar endDateSelectable = (Calendar) endMonth.clone();
        endDateSelectable.add(Calendar.DATE, -20);
        Log.d(TAG, "startDateSelectable: " + startDateSelectable.getTime().toString() + " :: endDateSelectable: " + endDateSelectable.getTime().toString());
        calendar.setSelectableDateRange(startDateSelectable, endDateSelectable);

        final Calendar startSelectedDate = (Calendar) startDateSelectable.clone();
        startSelectedDate.add(Calendar.DATE, 10);
        final Calendar endSelectedDate = (Calendar) endDateSelectable.clone();
        endSelectedDate.add(Calendar.DATE, -10);
        Log.d(TAG, "startSelectedDate: " + startSelectedDate.getTime().toString() + " :: endSelectedDate: " + endSelectedDate.getTime().toString());
        calendar.setSelectedDateRange(startSelectedDate, endSelectedDate);

        final Calendar current = (Calendar) startMonth.clone();
        current.add(Calendar.MONTH, 1);
        calendar.setCurrentMonth(current);*/
//        calendar.setFixedDaysSelection(2);
    }

    private val calendarListener: CalendarListener = object : CalendarListener {
        override fun onFirstDateSelected(startDate: Calendar) {
            Toast.makeText(
                this@DAteRange,
                "Start Date: " + startDate.time.toString(),
                Toast.LENGTH_SHORT
            ).show()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            check_in?.text = "C_in " + sdf.format(startDate.time)
            check_out?.text = "C_out " + sdf.format(startDate.time)
            //Log.d(TAG, "Selected dates: Start: " + printDate(calendar.getStartDate()) +" End:" + printDate(calendar.getEndDate()));
        }

        override fun onDateRangeSelected(startDate: Calendar, endDate: Calendar) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            check_in?.text = "C_in " + sdf.format(startDate.time)
            check_out?.text = "C_out " + sdf.format(endDate.time)
            Toast.makeText(
                this@DAteRange,
                "Start Date: " + startDate.time + " End date: " + endDate.time.toString(),
                Toast.LENGTH_SHORT
            ).show()
            //Log.d(TAG, "Selected dates: Start: " + printDate(calendar.getStartDate()) +" End:" + printDate(calendar.getEndDate()));
        }
    }

    companion object {
        private val TAG = DAteRange::class.java.simpleName
    }
}