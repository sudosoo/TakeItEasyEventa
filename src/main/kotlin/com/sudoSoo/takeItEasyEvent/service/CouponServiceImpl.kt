package com.sudoSoo.takeItEasyEvent.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.sudoSoo.takeItEasyEvent.common.annotation.DistributeLock
import com.sudoSoo.takeItEasyEvent.dto.CouponIssuanceRequestDto
import com.sudoSoo.takeItEasyEvent.dto.CreateCouponRequestDto
import com.sudoSoo.takeItEasyEvent.entity.Coupon
import com.sudoSoo.takeItEasyEvent.repository.CouponRepository
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
@Transactional
class CouponServiceImpl (
    val couponRepository: CouponRepository,
    val eventService: EventService,
    val redissonClient: RedissonClient ) : CouponService {

    val objectMapper = ObjectMapper()
    fun createCoupon(requestDto: CreateCouponRequestDto) {
        validateDiscountFields(requestDto)
        val coupon = if (requestDto.discountRate == 0) {
            // 할인율 적용 쿠폰일 때
            priceCouponCreate(requestDto)
        } else {
            // 할인가격 적용 쿠폰일 때
            rateCouponCreate(requestDto)
        }
        val event = eventService.getInstanceByName(requestDto.eventName)
        event.addCoupon(coupon)
        couponRepository.save(coupon);
    }

    private fun validateDiscountFields(requestDto: CreateCouponRequestDto) {
        if ((requestDto.discountRate == 0 && requestDto.discountPrice == 0L) ||
            (requestDto.discountRate != 0 && requestDto.discountPrice != 0L)) {
            throw IllegalArgumentException("discountRate 또는 discountPrice 중 하나만 존재 해야 합니다.")
        }
    }
    override fun priceCouponCreate(requestDto: CreateCouponRequestDto) : Coupon {
        return Coupon.priceOf(requestDto)
    }

    override fun rateCouponCreate(requestDto: CreateCouponRequestDto): Coupon {
        return Coupon.rateOf(requestDto)
    }

    @DistributeLock(lockName = "coupon")
    override fun couponIssuance(requestDto: CouponIssuanceRequestDto) :Int{
        val coupon : Coupon = couponRepository.findById(requestDto.couponId).orElseThrow { IllegalArgumentException("존재 하지 않는 쿠폰 입니다.") }
        coupon.issueToMember(requestDto.memberId)
        coupon.decreaseCouponQuantity()
        couponRepository.save(coupon)
        return coupon.couponQuantity
    }

    @Scheduled(fixedRate = 1000, initialDelay = 1_000 * 60 * 5)
    override fun couponIssuanceV2(requestDto: CouponIssuanceRequestDto) {
        val coupon : Coupon = couponRepository.findById(requestDto.couponId).orElseThrow { IllegalArgumentException("존재 하지 않는 쿠폰 입니다.") }
        coupon.issueToMember(requestDto.memberId)
        coupon.decreaseCouponQuantity()
        couponRepository.save(coupon)
    }


    private val WAIT_QUEUE_KEY = "waitingQ"
    private val PROCESS_QUEUE = "processQ"
    private val WAIT_TIME = 5L
    private val LEASE_TIME = 2L

    fun eventProcessingV1(requestDto: CouponIssuanceRequestDto)  {

    }

    fun eventProcessingV2()  {
        var memberRank = 0 // 시작
        val memberRankEnd = 100 // 수행할 데이터의 개수

        while (true) {
            val rLock: RLock = redissonClient.getLock(PROCESS_QUEUE)

            try {
                var isLock = rLock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS)
                val dataBatch = redissonClient
                    .getScoredSortedSet<String>(WAIT_QUEUE_KEY)
                    .valueRange(memberRank, memberRankEnd + memberRankEnd - 1)

                if (isLock != true) {
                    return
                }

                // 가져온 데이터가 비어있으면 모두 소진된 것으로 간주하고 반복 종료
                if (dataBatch.isEmpty()) {
                    break
                }
                // 성공한 사람들
                if (memberRank == 0) {
                    processDataBatch(dataBatch)
                }
                // 실패한사람들
                else {
                    falseProcessDataBatch(dataBatch)
                }

                // 다음 데이터를 가져오기 위해 offset을 증가시킴
                memberRank += memberRankEnd


            } catch (e: InterruptedException) {
                // 예외 발생 시 처리
                e.printStackTrace();
                println("An error occurred: ${e.message}")

                break // 예외 발생 시 반복 종료
            } finally {
                if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                    rLock.unlock()
                }
            }
        }
    }
    private fun processDataBatch(dataBatch: MutableCollection<String>) {
        dataBatch.forEach { e ->
            var element = objectMapper.readValue(e, CouponIssuanceRequestDto::class.java)
            couponIssuance(element)
        }
    }

    private fun falseProcessDataBatch(dataBatch: MutableCollection<String>) {
        dataBatch.forEach { e ->
            var element = objectMapper.readValue(e, CouponIssuanceRequestDto::class.java)
            println("쿠폰획득에 실패 하였습니다 userId : ${element.memberId}")
        }
    }
}

