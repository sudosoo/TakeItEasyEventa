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
    var memberId : Long? = null ,
    @OneToMany(mappedBy = "event")
    var event : Event ?= null,
    var discountPrice: Long = 0,
    var discountRate: Int = 0,
    var useCheck: Boolean = false
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
                discountRate = requestDto.discountRate)
        }

    }

    fun IssueToMember(memberId: Long?){
        this.memberId = memberId
    }
}