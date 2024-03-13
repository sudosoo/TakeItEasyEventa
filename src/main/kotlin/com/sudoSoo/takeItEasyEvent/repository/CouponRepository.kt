package com.sudoSoo.takeItEasyEvent.repository

import com.sudoSoo.takeItEasyEvent.common.repository.BaseRepository
import com.sudoSoo.takeItEasyEvent.entity.Coupon
import org.springframework.data.jpa.repository.JpaRepository

interface CouponRepository : BaseRepository<Coupon, Long>
