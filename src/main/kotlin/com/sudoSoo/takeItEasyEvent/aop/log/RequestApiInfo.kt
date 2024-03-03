package com.sudoSoo.takeItEasyEvent.aop.log

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.reflect.CodeSignature
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class RequestApiInfo(joinPoint: JoinPoint, clazz: Class<*>, objectMapper: ObjectMapper) {
    var method: String? = null
    var url: String? = null
    var name: String? = null
    val header: MutableMap<String, String> = HashMap()
    val body: MutableMap<String, String> = HashMap()
    val parameters: MutableMap<String, String> = HashMap()
    var ipAddress: String? = null
    val dateTime: String = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

    init {
        try {
            setRequestDetails()
            setApiInfo(joinPoint, clazz)
            setInputStream(joinPoint, objectMapper)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setRequestDetails() {
        try {
            val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
            setHeader(request)
            setIpAddress(request)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setHeader(request: HttpServletRequest) {
        val headerNames: Enumeration<String> = request.getHeaderNames()
        while (headerNames.hasMoreElements()) {
            val headerName: String = headerNames.nextElement()
            header[headerName] = request.getHeader(headerName)
        }
    }

    private fun setIpAddress(request: HttpServletRequest) {
        ipAddress = Optional.of(request).map { httpServletRequest: HttpServletRequest ->
            Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .orElse(Optional.ofNullable(request.getHeader("Proxy-Client-IP"))
                    .orElse(Optional.ofNullable(request.getHeader("WL-Proxy-Client-IP"))
                        .orElse(Optional.ofNullable(request.getHeader("HTTP_CLIENT_IP"))
                            .orElse(Optional.ofNullable(request.getHeader("HTTP_X_FORWARDED_FOR"))
                                .orElse(request.remoteAddr)))))
        }.orElse(null)
    }

    private fun setApiInfo(joinPoint: JoinPoint, clazz: Class<*>) {
        val methodSignature = joinPoint.signature as MethodSignature
        val method = methodSignature.method
        val requestMapping = clazz.getAnnotation(RequestMapping::class.java)
        val baseUrl = requestMapping.value[0]
        val methodAnnotations = listOf(
            GetMapping::class.java,
            PutMapping::class.java,
            PostMapping::class.java,
            DeleteMapping::class.java,
            RequestMapping::class.java
        )
        for (mappingClass in methodAnnotations) {
            if (method.isAnnotationPresent(mappingClass)) {
                val annotation: Annotation = method.getAnnotation(mappingClass)
                try {
                    val methodUrl = mappingClass.getMethod("value").invoke(annotation) as Array<String>
                    this.method = mappingClass.simpleName.replace("Mapping", "").uppercase(Locale.getDefault())
                    this.url = String.format("%s%s", baseUrl, if (methodUrl.isNotEmpty()) methodUrl[0] else "")
                    this.name = mappingClass.getMethod("name").invoke(annotation) as String
                    break
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    private fun setInputStream(joinPoint: JoinPoint, objectMapper: ObjectMapper) {
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
        try {
            val codeSignature = joinPoint.signature as CodeSignature
            val parameterNames = codeSignature.parameterNames
            val args = joinPoint.args
            for (i in parameterNames.indices) {
                if (parameterNames[i] == "request") {
                    body.putAll(objectMapper.convertValue(args[i], object : TypeReference<Map<String, String>>() {}))
                } else {
                    val json = objectMapper.writeValueAsString(args[i])
                    parameters[parameterNames[i]] = json
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}