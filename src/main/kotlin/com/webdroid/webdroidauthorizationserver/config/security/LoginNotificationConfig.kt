package com.webdroid.webdroidauthorizationserver.config.security

import com.maxmind.geoip2.DatabaseReader
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.util.ResourceUtils
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
//        val resource = File("src/main/resources/maxmind/GeoLite2-City.mmdb")
//        val resource2 = File("src/main/resources/maxmind/GeoLite2-Country.mmdb")
//        logger.info("GeoIPCity: ${resource.absolutePath}")
//        logger.info("GeoIPCountry: ${resource2.absolutePath}")
        val database = ResourceUtils
            .getFile("classpath:maxmind/GeoLite2-City.mmdb")
        val database2 = ResourceUtils
            .getFile("classpath:maxmind/GeoLite2-Country.mmdb")
        logger.info("GeoIPCity database: ${database.absolutePath}")
        logger.info("GeoIPCountry database: ${database2.absolutePath}")
        return DatabaseReader.Builder(database)
            .build()
    }
}