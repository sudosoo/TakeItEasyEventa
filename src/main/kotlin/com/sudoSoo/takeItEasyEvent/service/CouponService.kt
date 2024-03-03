package com.sudoSoo.takeItEasyEvent.service

import com.sudoSoo.takeItEasyEvent.dto.CouponEventRedisRequest
import com.sudoSoo.takeItEasyEvent.dto.CouponIssuanceRequestDto
import com.sudoSoo.takeItEasyEvent.dto.CreateCouponRequestDto
import com.sudoSoo.takeItEasyEvent.dto.CreateEventRequestDto
import com.sudoSoo.takeItEasyEvent.entity.Coupon

interface CouponService {
    fun create(requestDto: CreateCouponRequestDto)
    fun couponIssuance(requestDto: CouponIssuanceRequestDto) : Int
    fun couponIssuanceV2(requestDto: CouponIssuanceRequestDto)
}
