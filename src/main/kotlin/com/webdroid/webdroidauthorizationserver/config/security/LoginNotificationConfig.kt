package com.webdroid.webdroidauthorizationserver.config.security

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ua_parser.Parser
import java.io.IOException


@Configuration
class LoginNotificationConfig {
    private val logger = LoggerFactory.getLogger(javaClass)
    @Bean
    @Throws(IOException::class)
    fun uaParser(): Parser {
        return Parser()
    }
}