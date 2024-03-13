package com.sudoSoo.takeItEasyEvent.repository

import com.sudoSoo.takeItEasyEvent.common.repository.BaseRepository
import com.sudoSoo.takeItEasyEvent.entity.Event
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface EventRepository : BaseRepository<Event, Long> {
    fun findByName(eventName: String): Optional<Event>
}
