package com.sudoSoo.takeItEasyEvent.dto

class CreateCouponRequestDto(
    val eventName : String,
    val couponDeadline: String,
    val couponQuantity : Int,
    var discountRate : Int = 0,
    var discountPrice: Long = 0) {
}