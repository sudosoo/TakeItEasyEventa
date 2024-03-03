package com.sudoSoo.takeItEasyEvent.aop.log

import com.fasterxml.jackson.databind.ObjectMapper
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Aspect
@Component
class LoggingAspect(private val objectMapper: ObjectMapper) {

    companion object {
        private val logger = LoggerFactory.getLogger(LoggingAspect::class.java)
    }

    // 모든 컨트롤러 && NotLogging 어노테이션 미설정 시 로그 수집
    @Pointcut("within(*..*Controller) && !@annotation(com.sudoSoo.takeItEasyEvent.common.annotation.NotLogging)")
    fun onRequest() {}

    @Around("onRequest()")
    fun requestLogging(joinPoint: ProceedingJoinPoint): Any? {
        // API 요청 정보
        val apiInfo = RequestApiInfo(joinPoint, joinPoint.target.javaClass, objectMapper)

        // 로그 정보
        val logInfo =
            apiInfo.url?.let {
                apiInfo.name?.let { it1 ->
                    apiInfo.method?.let { it2 ->
                        apiInfo.ipAddress?.let { it3 ->
                            LogInfo(it, it1, it2,
                            apiInfo.header,
                            objectMapper.writeValueAsString(apiInfo.parameters).replace("\\", ""),
                            objectMapper.writeValueAsString(apiInfo.body).replace("\\", ""),
                            it3
                        )
                    }
                }
            }
        }
        try {
            val result = joinPoint.proceed(joinPoint.args)

            // Method가 Get이 아닌 로그만 수집
            if (logInfo != null) {
                if (logInfo.method != "GET") {
                    val logMessage = objectMapper.writeValueAsString(mapOf("logInfo" to logInfo))
                    logger.info(logMessage)
                }
            }
            return result

        } catch (e: Exception) {
            val exceptionAsString = e.printStackTrace().toString()

            // 발생 Exception 설정
            if (logInfo != null) {
                logInfo.exception = exceptionAsString
            }
            val logMessage = objectMapper.writeValueAsString(logInfo)
            logger.error(logMessage)

            throw e
        }
    }
}
