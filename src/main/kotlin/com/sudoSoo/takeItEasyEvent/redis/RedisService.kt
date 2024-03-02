package com.sudoSoo.takeItEasyEvent.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.sudoSoo.takeItEasyEvent.dto.CouponEventRedisRequest
import com.sudoSoo.takeItEasyEvent.dto.CouponIssuanceRequestDto
import com.sudoSoo.takeItEasyEvent.dto.CreateEventRequestDto
import com.sudoSoo.takeItEasyEvent.service.CouponService
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.concurrent.TimeUnit


@Service
class RedisService(
    val redisTemplate : RedisTemplate<String,String>,
    val objectMapper: ObjectMapper,
    val redisClient : RedissonClient,
    val couponService: CouponService
    ) {
    private val WAIT_QUEUE_KEY = "waitingQ"
    private val PROCESS_QUEUE = "processQ"
    private val WAIT_TIME = 5L
    private val LEASE_TIME = 2L

    fun register(requestDto: CouponEventRedisRequest): Int {
        val jsonConvertDto = objectMapper.writeValueAsString(requestDto)
        return redisClient
            .getScoredSortedSet<String>(WAIT_QUEUE_KEY)
            .addAndGetRank(Instant.now().toEpochMilli().toDouble(), jsonConvertDto)
    }

    fun eventProcessing() {
        var memberRank = 0 // 시작
        val memberRankEnd = 1000 // 수행할 데이터의 개수

        while (true) {
            val rLock: RLock = redisClient.getLock(PROCESS_QUEUE)

            try {
                var isLock = rLock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS)
                val dataBatch = redisClient
                    .getScoredSortedSet<String>(WAIT_QUEUE_KEY)
                    .valueRange(memberRank, memberRankEnd + memberRankEnd - 1)

                if (!isLock) {
                    return
                }


                // 가져온 데이터가 비어있으면 모두 소진된 것으로 간주하고 반복 종료
                if (dataBatch.isEmpty()) {
                    break
                }

                // 가져온 데이터를 처리하는 작업 수행
                if (memberRank == 0) {
                    processDataBatch(dataBatch)
                } else {
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
            couponService.issueToMember(element)
        }
    }

    private fun falseProcessDataBatch(dataBatch: MutableCollection<String>) {
        dataBatch.forEach { e ->
            var element = objectMapper.readValue(e, CouponIssuanceRequestDto::class.java)
            println("쿠폰획득에 실패 하였습니다 userId : ${element.memberId}")
        }
    }

//  대기큐 넣기

//메서드 시작 -> while 시작 (레디스가 모두 비워질때까지 반복)->
//락 획득 -> 앞에서 100개 가져오기 - >  100개 처리 -> 락 해제 --반복


}