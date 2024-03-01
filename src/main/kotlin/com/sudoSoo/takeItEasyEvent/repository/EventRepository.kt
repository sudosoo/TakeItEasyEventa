package com.sudoSoo.takeItEasyEvent.repository

import com.sudoSoo.takeItEasyEvent.entity.Event
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface EventRepository : JpaRepository<Event, Long> {
}
