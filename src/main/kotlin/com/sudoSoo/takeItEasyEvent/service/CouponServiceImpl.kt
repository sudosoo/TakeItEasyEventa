package com.sudoSoo.takeItEasyEvent.service

import com.sudoSoo.takeItEasyEvent.dto.CouponIssuanceRequestDto
import com.sudoSoo.takeItEasyEvent.dto.CreateEventRequestDto
import com.sudoSoo.takeItEasyEvent.entity.Coupon
import com.sudoSoo.takeItEasyEvent.repository.CouponRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CouponServiceImpl (val couponRepository: CouponRepository) : CouponService {

    override fun priceCouponCreate(requestDto: CreateEventRequestDto) : Coupon {
        val coupon = Coupon.priceFactory(requestDto)
        return couponRepository.save(coupon);
    }

    override fun rateCouponCreate(requestDto: CreateEventRequestDto): Coupon {
        val coupon = Coupon.rateFactory(requestDto)
        return couponRepository.save(coupon);
    }

    override fun issueToMember(requestDto: CouponIssuanceRequestDto) {
        val coupon : Coupon = couponRepository.findById(requestDto.couponId).orElseThrow { IllegalArgumentException("존재 하지 않는 쿠폰 입니다.") }
        coupon.issueToMember(requestDto.memberId)
        couponRepository.save(coupon)
    }
}