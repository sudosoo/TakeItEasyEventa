package com.sudoSoo.takeItEasyEvent.redis

import org.redisson.Redisson
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.redisson.spring.data.connection.RedissonConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig (
    @Value("\${devsoo.ip}")
    val redisHost: String,

    @Value("\${spring.data.redis.port}")
    val redisPort : Int
){
    @Bean
    fun redissonConnectionFactory(redissonClient: RedissonClient): RedissonConnectionFactory {
        return RedissonConnectionFactory(redissonClient)
    }

    @Bean
    fun redissonClient():RedissonClient{
        val config = Config()
        config.useSingleServer().setAddress("redis://$redisHost:$redisPort")
        return Redisson.create(config)
    }

//
//    @Bean
//    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, String> {
//        val redisTemplate = RedisTemplate<String, String>()
//        redisTemplate.keySerializer = StringRedisSerializer()
//        redisTemplate.valueSerializer = StringRedisSerializer()
//        redisTemplate.setDefaultSerializer(RedisSerializer.string())
//        redisTemplate.connectionFactory = redisConnectionFactory
//        return redisTemplate
//    }
//
//    @Bean
//    fun redisMessageListener(connectionFactory: RedisConnectionFactory): RedisMessageListenerContainer {
//        val container = RedisMessageListenerContainer()
//        container.setConnectionFactory(connectionFactory)
//        return container
//    }
}