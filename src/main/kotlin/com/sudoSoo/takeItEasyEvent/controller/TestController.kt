package com.sudoSoo.takeItEasyEvent.controller

import com.sudoSoo.takeItEasyEvent.dto.CouponEventRedisRequest
import com.sudoSoo.takeItEasyEvent.redis.RedisService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/redis")
class TestController (val redisService: RedisService){

    @PostMapping("/test")
    fun getMemberList(@RequestBody requestDto: CouponEventRedisRequest): ResponseEntity<Void> {
        val score =  redisService.register(requestDto)
        println(score)
        return ResponseEntity.ok().build()
    }
}