package com.sudoSoo.takeItEasyEvent.dto

class CouponIssuanceResponseDto(var eventId: Long, var couponId: Long, var memberId: Long, var message: String) {
    constructor(requestDto: CouponIssuanceRequestDto, message: String) : this(
        requestDto.eventId,
        requestDto.couponId,
        requestDto.memberId,
        message
    )
}
