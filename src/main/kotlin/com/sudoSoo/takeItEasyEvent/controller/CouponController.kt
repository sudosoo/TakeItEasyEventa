package com.sudoSoo.takeItEasyEvent.controller

import com.sudoSoo.takeItEasyEvent.dto.CouponEventRedisRequest
import com.sudoSoo.takeItEasyEvent.dto.CouponIssuanceRequestDto
import com.sudoSoo.takeItEasyEvent.dto.CreateCouponRequestDto
import com.sudoSoo.takeItEasyEvent.entity.Coupon
import com.sudoSoo.takeItEasyEvent.redis.RedisService
import com.sudoSoo.takeItEasyEvent.service.CouponService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/coupon")
class CouponController (val couponService: CouponService){

    @PostMapping("/create")
    fun couponCreate(@RequestBody requestDto: CreateCouponRequestDto): ResponseEntity<Void> {
        couponService.create(requestDto)
        return ResponseEntity.ok().build()
    }

    @PutMapping("/Issuance")
    fun couponIssuance(@RequestBody requestDto: CouponIssuanceRequestDto):ResponseEntity<Void>{
        couponService.couponIssuance(requestDto)
        return ResponseEntity.ok().build()
    }


}