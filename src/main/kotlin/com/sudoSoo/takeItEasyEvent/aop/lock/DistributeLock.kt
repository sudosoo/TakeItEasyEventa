package com.sudoSoo.takeItEasyEvent.aop.lock

import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DistributeLock (
    val lockName: String,
    val timeUnit: TimeUnit = TimeUnit.SECONDS,
    val waitTime: Long = 5L,
    val leaseTime: Long = 3L ){
}