package com.webdroid.webdroidauthorizationserver

import com.webdroid.webdroidauthorizationserver.config.AppProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AppProperties::class)
class WebdroidAuthorizationServerApplication

fun main(args: Array<String>) {
    runApplication<WebdroidAuthorizationServerApplication>(*args)
}
