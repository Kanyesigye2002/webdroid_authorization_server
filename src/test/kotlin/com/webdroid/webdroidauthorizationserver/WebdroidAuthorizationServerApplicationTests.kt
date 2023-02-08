package com.webdroid.webdroidauthorizationserver

import com.webdroid.webdroidauthorizationserver.config.AppProperties
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class WebdroidAuthorizationServerApplicationTests {

    @Autowired
    lateinit var appConfig: AppProperties
    private val logger = LoggerFactory.getLogger(javaClass)

    @Test
    fun contextLoads() {
        logger.debug("App properties @Pavel: ${appConfig.frontendUrl}")
    }

    @Test
    fun testProperties() {
        logger.info("App properties @Pavel: ${appConfig.frontendUrl}")
    }

}
