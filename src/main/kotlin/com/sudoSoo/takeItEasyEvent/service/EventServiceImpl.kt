package com.sudoSoo.takeItEasyEvent.service

import com.sudoSoo.takeItEasyEvent.dto.CouponIssuanceRequestDto
import com.sudoSoo.takeItEasyEvent.dto.CreateEventRequestDto
import com.sudoSoo.takeItEasyEvent.entity.Coupon
import com.sudoSoo.takeItEasyEvent.entity.Event
import com.sudoSoo.takeItEasyEvent.repository.EventRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class EventServiceImpl (val eventRepository:EventRepository,val couponService: CouponService) :EventService {
    override fun createEvent(requestDto: CreateEventRequestDto) {
        validateDiscountFields(requestDto)
        val coupon: Coupon = if (requestDto.discountRate != 0) {
            // 할인율 적용 쿠폰일 때
            couponService.rateCouponCreate(requestDto)
        } else {
            // 할인가격 적용 쿠폰일 때
            couponService.priceCouponCreate(requestDto)
        }

        val event = Event.of(requestDto)
        event.addCoupon(coupon)
        eventRepository.save(event)

    }

    private fun validateDiscountFields(requestDto: CreateEventRequestDto) {
        if ((requestDto.discountRate == 0 && requestDto.discountPrice == 0L) ||
            (requestDto.discountRate != 0 && requestDto.discountPrice != 0L)) {
            throw IllegalArgumentException("discountRate 또는 discountPrice 중 하나만 존재 해야 합니다.")
        }
    }

    @Transactional(timeout = 5)
    override fun couponIssuance(requestDto: CouponIssuanceRequestDto) {
        val event = eventRepository.findByEventIdForUpdate(requestDto.eventId)
            ?: throw IllegalArgumentException("Event is not found")
        couponService.issueToMember(requestDto)
        event.decreaseCouponQuantity()
        eventRepository.save(event)
    }

}