package com.sudoSoo.takeItEasyEvent.entity

import com.sudoSoo.takeItEasyEvent.dto.CreateEventRequestDto
import jakarta.persistence.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
class Coupon(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,
    val couponName: String,
    val couponDeadline: LocalDateTime,
    var memberId : Long? = null,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    var event : Event ?= null,
    var discountPrice: Long = 0,
    var discountRate: Int = 0,
    var useCheck: Boolean = false,
    var couponQuantity : Int = 0
) {
    init {

        this.useCheck = useCheck
    }

    companion object {
        fun priceFactory(requestDto: CreateEventRequestDto): Coupon {
            val couponName = "${requestDto.eventName}_${requestDto.discountPrice}"
            val couponDeadline = LocalDateTime.parse(
                requestDto.couponDeadline,
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            )
            return Coupon(
                couponName = couponName,
                couponDeadline = couponDeadline,
                couponQuantity = requestDto.couponQuantity,
                discountPrice = requestDto.discountPrice)
        }


        fun rateFactory(requestDto: CreateEventRequestDto): Coupon {
            val couponName = "${requestDto.eventName}_${requestDto.discountRate}"
            val couponDeadline = LocalDateTime.parse(
                requestDto.couponDeadline,
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
            )
            return Coupon(
                couponName = couponName,
                couponDeadline = couponDeadline,
                couponQuantity = requestDto.couponQuantity,
                discountRate = requestDto.discountRate)
        }
    }

    fun decreaseCouponQuantity() {
        validQuantity()
        couponQuantity--
    }

    private fun validQuantity() {
        if (couponQuantity < 1) {
            throw IllegalStateException("남은 쿠폰 수량이 없습니다.")
        }
    }

    fun issueToMember(memberId: Long?){
        this.memberId = memberId
    }
}