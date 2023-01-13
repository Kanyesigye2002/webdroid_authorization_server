package com.webdroid.webdroidauthorizationserver.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
class AppProperties {
    @JvmField
    val auth = Auth()
    @JvmField
    val oauth2 = OAuth2()

    class Auth {
        @JvmField
        var tokenSecret: String? = null
        @JvmField
        var tokenExpirationMsec: Long = 0
    }

    class OAuth2 {
        var authorizedRedirectUris: List<String> = ArrayList()
            private set

        fun authorizedRedirectUris(authorizedRedirectUris: List<String>): OAuth2 {
            this.authorizedRedirectUris = authorizedRedirectUris
            return this
        }
    }
}