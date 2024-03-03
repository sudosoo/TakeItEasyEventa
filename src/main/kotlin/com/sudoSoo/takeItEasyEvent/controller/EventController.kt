package com.sudoSoo.takeItEasyEvent.controller

import com.sudoSoo.takeItEasyEvent.dto.CouponEventRedisRequest
import com.sudoSoo.takeItEasyEvent.dto.CreateEventRequestDto
import com.sudoSoo.takeItEasyEvent.redis.RedisService
import com.sudoSoo.takeItEasyEvent.service.EventService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/event")
class EventController (val eventService: EventService){

    @PostMapping("/create")
    fun createEvent(@RequestBody requestDto: CreateEventRequestDto): ResponseEntity<Void> {
        eventService.create(requestDto)
        return ResponseEntity.ok().build()
    }

}