package com.sudoSoo.takeItEasyEvent.repository

import com.sudoSoo.takeItEasyEvent.entity.Event
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query

interface EventRepository : JpaRepository<Event, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.memberId IS NULL AND e.id = :eventId")
    fun findByEventIdForUpdate(eventId: Long): Event?
}
