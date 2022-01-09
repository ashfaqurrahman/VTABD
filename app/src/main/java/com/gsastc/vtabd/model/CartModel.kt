package com.gsastc.vtabd.model

data class CartModel(
    var id: Int? = null,
    var name: String? = null,
    var type: String? = null,
    var checkInCheckOut: String? = null,
    var rantPerDay: String? = null,
    var days: String? = null,
    var totalAmount: String? = null
)