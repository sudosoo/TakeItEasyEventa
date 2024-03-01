package com.sudoSoo.takeItEasyEvent.redis

import com.fasterxml.jackson.databind.ObjectMapper
import com.sudoSoo.takeItEasyEvent.dto.CouponEventRedisRequest
import org.redisson.api.RedissonClient
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.Instant


@Service
class RedisService(
    val redisTemplate : RedisTemplate<String,String>,
    val objectMapper: ObjectMapper,
    val redisClient : RedissonClient
    ) {
    private val WAIT_QUEUE_KEY = "waiting_queue"

    fun register (requestDto: CouponEventRedisRequest): Int {
        val jsonConvertDto = objectMapper.writeValueAsString(requestDto)
        return redisClient
            .getScoredSortedSet<String>(WAIT_QUEUE_KEY)
            .addAndGetRank(Instant.now().toEpochMilli().toDouble(),jsonConvertDto)
    }

//    fun addToSortedSet(requestDto: CouponEventRedisRequest) {
//        val currentTimeMillis = Instant.now().toEpochMilli().toDouble()
//        val jsonConvertDto = objectMapper.writeValueAsString(requestDto)
//        redisTemplate.opsForZSet().add(WAIT_QUEUE_KEY, jsonConvertDto, currentTimeMillis)
//        println("Member added to sorted set with score: $currentTimeMillis")
//    }


}