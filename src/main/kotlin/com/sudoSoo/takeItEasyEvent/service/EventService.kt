package com.sudoSoo.takeItEasyEvent.service

import com.sudoSoo.takeItEasyEvent.dto.CouponIssuanceRequestDto
import com.sudoSoo.takeItEasyEvent.dto.CreateEventRequestDto

interface EventService {
    fun createEvent(requestDto: CreateEventRequestDto)
    fun couponIssuance(requestDto: CouponIssuanceRequestDto)
}
