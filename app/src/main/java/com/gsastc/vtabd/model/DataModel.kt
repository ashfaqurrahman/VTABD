package com.gsastc.vtabd.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class DataModel(
    //common
    var address: String? = null,
    var currentUserID: String? = null,
    var city: String? = null,
    var cityName: String? = null,
    var division: String? = null,
    var checkInCheckOut: String? = null,
    var days: String? = null,
    var description: String? = null,
    var district: String? = null,
    var role: String? = null,
    var phone: String? = null,
    var imageUri: String? = null,
    var imageUrl: String? = null,
    var name: String? = null,
    var orderKey: String? = null,
    var rantPerDay: String? = null,
    var status: String? = null,
    var slag: String? = null,
    var type: String? = null,
    var totalAmount: String? = null,
    var targetedUserID: String? = null,
    var visibility: String? = null,

    //place
    var placeName: String? = null,
    var userName: String? = null,
    var myEmail: String? = null,

    //district
    var districtKey: String? = null,
    var districtName: String? = null,

    //hotel
    var galleryKey: String? = null,
    var hotelName: String? = null,
    var hotelDescription: String? = null,
    var hotelPhone: String? = null,
    var hotelID: String? = null,

    var roomKey: String? = null,
    var roomNo: String? = null,
    var capacity: String? = null,
    var price: String? = null,
    var ac: String? = null,
    var washRoom: String? = null,

    //author
    var authorID: String? = null,
    var authorName: String? = null,

    //blog
    var blogName: String? = null,
    var blogKey: String? = null,
    var date: String? = null,

    //guide
    var guideEmail: String? = null,
    var guidePhone: String? = null,
    var guideName: String? = null,
    var guideRant: String? = null,
    var uid: String? = null,
    var time: String? = null,

    //transport
    var vehicleModelNane: String? = null,
    var vehicleKey: String? = null,

    var transportName: String? = null,
    var transportID: String? = null

)