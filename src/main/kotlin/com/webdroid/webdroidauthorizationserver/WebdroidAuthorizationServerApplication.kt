package com.webdroid.webdroidauthorizationserver

import com.webdroid.webdroidauthorizationserver.config.AppProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.context.request.RequestContextListener

@SpringBootApplication
@EnableConfigurationProperties(AppProperties::class)
class WebdroidAuthorizationServerApplication

fun main(args: Array<String>) {
    runApplication<WebdroidAuthorizationServerApplication>(*args)
}

@Bean
fun requestContextListener(): RequestContextListener {
    return RequestContextListener()
}
