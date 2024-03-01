package com.sudoSoo.takeItEasyEvent

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableJpaAuditing
@EnableTransactionManagement
class TakeItEasyEventApplication

fun main(args: Array<String>) {
	runApplication<TakeItEasyEventApplication>(*args)
}
