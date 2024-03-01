package com.sudoSoo.takeItEasyEvent.service

import com.sudoSoo.takeItEasyEvent.dto.CouponIssuanceRequestDto
import com.sudoSoo.takeItEasyEvent.dto.CreateEventRequestDto
import com.sudoSoo.takeItEasyEvent.entity.Coupon
import com.sudoSoo.takeItEasyEvent.entity.Event
import com.sudoSoo.takeItEasyEvent.repository.EventRepository
import org.springframework.scheduling.annotation.Scheduled
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

    override fun couponIssuance(requestDto: CouponIssuanceRequestDto) {
        var event = eventRepository.findById(requestDto.eventId).orElseThrow {throw IllegalArgumentException("해당 이벤트는 존재하지 않습니다")}
        couponService.issueToMember(requestDto)
        event.decreaseCouponQuantity()
        eventRepository.save(event)
    }


    //실행시키면 5분 후 1초에 한번씩 실행이 됌
    @Scheduled(fixedRate = 1000, initialDelay = 1_000 * 60 * 5)
    override fun redisToLocalQueueSchedule(requestDto: CouponIssuanceRequestDto) {

        //큐에서 빼온 친구들 쿠폰이랑 연결시키고 쿠폰갯수 끝나면 대기큐에 있는사람들에게 꽝 넣어주기
        var event = eventRepository.findById(requestDto.eventId).orElseThrow {throw IllegalArgumentException("해당 이벤트는 존재하지 않습니다")}
        couponService.issueToMember(requestDto)
        event.decreaseCouponQuantity()
        eventRepository.save(event)
    }

}