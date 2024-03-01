package com.sudoSoo.takeItEasyEvent.dto

class CreateEventRequestDto(val eventName: String,
                            val eventDeadline: String,
                            val couponDeadline: String,
                            val couponQuantity : Int,
                            var discountRate : Int = 0,
                            var discountPrice: Long = 0){

}
