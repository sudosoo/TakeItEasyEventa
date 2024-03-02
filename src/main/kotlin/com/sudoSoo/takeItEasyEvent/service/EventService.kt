package com.sudoSoo.takeItEasyEvent.service

import com.sudoSoo.takeItEasyEvent.dto.CouponIssuanceRequestDto
import com.sudoSoo.takeItEasyEvent.dto.CreateEventRequestDto
import com.sudoSoo.takeItEasyEvent.entity.Event

interface EventService {
    fun createEvent(requestDto: CreateEventRequestDto)
    fun getInstanceByName(eventName : String) : Event
}
