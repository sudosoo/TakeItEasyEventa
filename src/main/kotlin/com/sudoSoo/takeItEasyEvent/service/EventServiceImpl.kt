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
class EventServiceImpl (val eventRepository:EventRepository) :EventService {
    override fun createEvent(requestDto: CreateEventRequestDto) {
        val event = Event.of(requestDto)
        eventRepository.save(event)
    }


    override fun getInstanceByName(eventName: String) : Event{
        return eventRepository.findByName(eventName)
            .orElseThrow{IllegalArgumentException("존재하지 않는 이벤트 입니다")}
    }


}