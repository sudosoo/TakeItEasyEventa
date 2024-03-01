package com.sudoSoo.takeItEasyEvent.service

import com.sudoSoo.takeItEasyEvent.dto.CouponIssuanceRequestDto
import com.sudoSoo.takeItEasyEvent.dto.CreateEventRequestDto
import com.sudoSoo.takeItEasyEvent.entity.Coupon

interface CouponService {
    fun priceCouponCreate(requestDto: CreateEventRequestDto): Coupon
    fun rateCouponCreate(requestDto: CreateEventRequestDto): Coupon
    fun issueToMember(requestDto: CouponIssuanceRequestDto)
}
