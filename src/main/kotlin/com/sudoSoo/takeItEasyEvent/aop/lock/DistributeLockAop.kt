package com.sudoSoo.takeItEasyEvent.aop.lock

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.redisson.api.RLock
import org.redisson.api.RedissonClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class DistributeLockAop(val redissonClient: RedissonClient, val aopTransaction: AopTransaction) {
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)!!
        private const val REDISSON_KEY_PREFIX = "RLOCK_"
    }

    @Around("@annotation(com.sudoSoo.takeItEasyEvent.aop.lock.DistributeLock)")
    fun lock(joinPoint: ProceedingJoinPoint): Any {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        val distributeLock = method.getAnnotation(DistributeLock::class.java)

        val key = REDISSON_KEY_PREFIX + CustomSpringELParser.getDynamicValue(signature.parameterNames, joinPoint.args, distributeLock.lockName) // (2)

        val rLock: RLock = redissonClient.getLock(key)

        return try {
            val available = rLock.tryLock(distributeLock.waitTime, distributeLock.leaseTime, distributeLock.timeUnit)
            if (!available) {
                return false
            }
            log.info("get lock success {}" , key)
            aopTransaction.proceed(joinPoint)
        } catch (e: Exception) {
            Thread.currentThread().interrupt()
            throw InterruptedException()
        } finally {
            rLock.unlock()
        }
    }
}
