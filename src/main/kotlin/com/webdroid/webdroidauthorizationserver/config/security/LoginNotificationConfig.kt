package com.webdroid.webdroidauthorizationserver.config.security

import com.maxmind.geoip2.DatabaseReader
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

    @Bean(name = ["GeoIPCity"])
    @Throws(IOException::class)
    fun databaseReader(): DatabaseReader {
        val resource = this.javaClass.classLoader.getResourceAsStream("maxmind/GeoLite2-City.mmdb")
//        val resource = File("src/main/resources/maxmind/GeoLite2-City.mmdb")
//        val resource = ResourceUtils.getFile("classpath:maxmind/GeoLite2-City.mmdb")
        logger.info("GeoIPCity database: $resource")
        return DatabaseReader.Builder(resource)
            .build()
    }
}