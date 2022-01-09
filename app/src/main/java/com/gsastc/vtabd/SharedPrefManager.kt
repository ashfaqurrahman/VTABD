package com.gsastc.vtabd

import android.content.Context
import android.content.SharedPreferences

class SharedPrefManager private constructor(private var context: Context) {

    //this method will save your phone to shared preferences
    fun savePhone(phone: String?): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            SHARED_PREF_NAME, Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(PHONE, phone)
        editor.apply()
        return true
    }

    //this method will fetch your phone from shared preferences
    val getPhone: String?
        get() {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE
            )
            return sharedPreferences.getString(PHONE, null)
        }

    //this method will save your phone to shared preferences
    fun saveTargetedUserID(targetedUserID: String?): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            SHARED_PREF_NAME, Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(TARGETED_USER_ID, targetedUserID)
        editor.apply()
        return true
    }

    //this method will fetch your phone from shared preferences
    val getTargetedUserID: String?
        get() {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE
            )
            return sharedPreferences.getString(TARGETED_USER_ID, null)
        }

    //this method will save your current location to shared preferences
    fun saveCurrentLocation(location: String?): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            SHARED_PREF_NAME, Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(TAG_LOCATION, location)
        editor.apply()
        return true
    }

    //this method will fetch your current location from shared preferences
    val getCurrentLocation: String?
        get() {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE
            )
            return sharedPreferences.getString(TAG_LOCATION, null)
        }

    //this method will save your register status to shared preferences
    fun saveRegisterStatus(status: String?): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            SHARED_PREF_NAME, Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(STATUS, status)
        editor.apply()
        return true
    }

    //this method will fetch your register status from shared preferences
    val getRegisterStatus: String?
        get() {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE
            )
            return sharedPreferences.getString(STATUS, null)
        }

    //this method will save your name & pic to shared preferences
    fun saveUserNamePic(userName: String?, userPic: String?): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            SHARED_PREF_NAME, Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(USER_NAME, userName)
        editor.putString(USER_PIC, userPic)
        editor.apply()
        return true
    }

    //this method will fetch your name from shared preferences
    val getUserName: String?
        get() {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE
            )
            return sharedPreferences.getString(USER_NAME, null)
        }

    //this method will fetch your name from shared preferences
    val getUserPic: String?
        get() {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE
            )
            return sharedPreferences.getString(USER_PIC, null)
        }

    //this method will save hotel date range to shared preferences
    fun saveDateRange(dateRange: String?): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            SHARED_PREF_NAME, Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(TAG_HOTEL_DATE_RANGE, dateRange)
        editor.apply()
        return true
    }

    //this method will fetch hotel date range from shared preferences
    val getDateRange: String?
        get() {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE
            )
            return sharedPreferences.getString(TAG_HOTEL_DATE_RANGE, null)
        }

    //this method will save room and person no to shared preferences
    fun saveRoomPersonChild(room: Int, person: Int, child: Int): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            SHARED_PREF_NAME, Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt(ROOM, room)
        editor.putInt(PERSON, person)
        editor.putInt(CHILD, child)
        editor.apply()
        return true
    }

    //this method will fetch room no from shared preferences
    val getRoom: Int?
        get() {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE
            )
            return sharedPreferences.getInt(ROOM, 0)
        }

    //this method will fetch person no from shared preferences
    val getPerson: Int?
        get() {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE
            )
            return sharedPreferences.getInt(PERSON, 0)
        }

    //this method will fetch child no from shared preferences
    val getChild: Int?
        get() {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE
            )
            return sharedPreferences.getInt(CHILD, 0)
        }

    //this method will save room and person no to shared preferences
    fun saveHotelImageNameAddress(imageUrl: String?, hotelName: String?, hotelAddress: String?): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            SHARED_PREF_NAME, Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(HOTEL_IMAGE, imageUrl)
        editor.putString(HOTEL_NAME, hotelName)
        editor.putString(HOTEL_ADDRESS, hotelAddress)
        editor.apply()
        return true
    }

    //this method will fetch room no from shared preferences
    val getHotelImageUrl: String?
        get() {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE
            )
            return sharedPreferences.getString(HOTEL_IMAGE, null)
        }

    //this method will fetch person no from shared preferences
    val getHotelName: String?
        get() {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE
            )
            return sharedPreferences.getString(HOTEL_NAME, null)
        }

    //this method will fetch child no from shared preferences
    val getHotelAddress: String?
        get() {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE
            )
            return sharedPreferences.getString(HOTEL_ADDRESS, null)
        }

    //this method will save hotel date range to shared preferences
    fun saveDistrictDivision(district: String?, division: String?): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            SHARED_PREF_NAME, Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(DISTRICT, district)
        editor.putString(DIVISION, division)
        editor.apply()
        return true
    }

    //this method will fetch hotel date range from shared preferences
    val getDistrict: String?
        get() {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE
            )
            return sharedPreferences.getString(DISTRICT, null)
        }

    //this method will fetch hotel date range from shared preferences
    val getDivision: String?
        get() {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE
            )
            return sharedPreferences.getString(DIVISION, null)
        }

    //this method will save hotel date range to shared preferences
    fun saveDay(day: String?): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences(
            SHARED_PREF_NAME, Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putString(DAY, day)
        editor.apply()
        return true
    }

    //this method will fetch hotel date range from shared preferences
    val getDay: String?
        get() {
            val sharedPreferences: SharedPreferences = context.getSharedPreferences(
                SHARED_PREF_NAME, Context.MODE_PRIVATE
            )
            return sharedPreferences.getString(DAY, null)
        }

    companion object {
        private const val SHARED_PREF_NAME = "SharedPref"
        private const val TAG_LOCATION = "location"
        private const val STATUS = "status"
        private const val USER_NAME = "userName"
        private const val USER_PIC = "userPic"
        private const val PHONE = "phone"
        private const val TAG_HOTEL_DATE_RANGE = "Hotel_date_range"
        private const val ROOM = "Room"
        private const val PERSON = "Person"
        private const val CHILD = "Child"
        private const val HOTEL_IMAGE = "Hotel_image"
        private const val HOTEL_NAME = "Hotel_name"
        private const val HOTEL_ADDRESS = "Hotel_address"
        private const val DISTRICT = "District"
        private const val DIVISION = "Division"
        private const val DAY = "Day"
        private const val TARGETED_USER_ID = "Targeted_user_ID"
        private var mInstance: SharedPrefManager? = null
        @Synchronized
        fun getInstance(context: Context): SharedPrefManager? {
            if (mInstance == null) {
                mInstance = SharedPrefManager(context)
            }
            return mInstance
        }
    }
}