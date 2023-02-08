package com.webdroid.webdroidauthorizationserver.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
class AppProperties (
    val frontendUrl: String,
    val auth: Auth,
    val oauth2: OAuth2,
)

class Auth (
    var tokenSecret: String,
    var tokenExpirationMsec: Long,
)

class OAuth2 (
    var authorizedRedirectUris: List<String>
)