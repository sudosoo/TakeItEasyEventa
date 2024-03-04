package com.sudoSoo.takeItEasyEvent.service

import com.sudoSoo.takeItEasyEvent.dto.CouponIssuanceRequestDto
import com.sudoSoo.takeItEasyEvent.entity.Coupon
import com.sudoSoo.takeItEasyEvent.entity.Event
import com.sudoSoo.takeItEasyEvent.repository.CouponRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.redisson.api.RedissonClient
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger


class CouponServiceImplTest{
    val couponRepository: CouponRepository = mockk()
    val eventService: EventService = mockk()
    val redissonClient: RedissonClient = mockk()

    val couponService: CouponService = CouponServiceImpl(couponRepository,eventService,redissonClient)
    val event : Event = mockk<Event>()
    val coupon : Coupon = Coupon(1L,"testCoupon", LocalDateTime.now(),1L,event,10L,0,false,10)

    @BeforeEach
    fun setUp() {
        every { eventService.getInstanceByName(any()) } returns event
        every { couponRepository.findById(any())} returns Optional.of(coupon)

    }

    @Test
    @DisplayName("쿠폰 발급 테스트 (멀티 스레드) 10만명 중 10명")
    fun couponIssuance() {
        val numberOfThreads = 10000
        val executorService = Executors.newFixedThreadPool(10)
        val latch = CountDownLatch(numberOfThreads)
        val requestDto = CouponIssuanceRequestDto(1L,1L,1L)
        val successCount = AtomicInteger()

        for (i in 0 until numberOfThreads) {
            val threadNumber = i + 1
            executorService.execute {
                try {
                    println(threadNumber)
                    couponService.couponIssuance(requestDto)
                    successCount.incrementAndGet()
                    println("Thread $threadNumber - 성공")
                } catch (e: InterruptedException ) {
                    println("Thread $threadNumber - 락 충돌 감지")
                } catch (e: Exception) {
                    println("Thread $threadNumber - ${e.message}")
                }
                latch.countDown()
            }
        }
        latch.await()
        verify(exactly = 10) { couponRepository.save(coupon)}
        assertEquals(0, coupon.couponQuantity)
    }
}