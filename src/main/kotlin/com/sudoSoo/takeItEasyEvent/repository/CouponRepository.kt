package com.sudoSoo.takeItEasyEvent.repository

import com.sudoSoo.takeItEasyEvent.entity.Coupon
import org.springframework.data.jpa.repository.JpaRepository

interface CouponRepository : JpaRepository<Coupon, Long>
