package com.sudoSoo.takeItEasyEvent.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.sudoSoo.takeItEasyEvent.dto.CouponEventRedisRequest
import com.sudoSoo.takeItEasyEvent.dto.CouponIssuanceRequestDto
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
    val redisClient : RedissonClient
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

    fun getRank(requestDto : Any): Int{
        val convertJson = objectMapper.writeValueAsString(requestDto)
        return redisClient.getScoredSortedSet<String>(WAIT_QUEUE_KEY).rank(convertJson)
    }


}
