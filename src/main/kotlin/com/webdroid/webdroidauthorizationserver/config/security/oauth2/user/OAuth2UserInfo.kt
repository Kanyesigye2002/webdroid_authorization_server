package com.webdroid.webdroidauthorizationserver.config.security.oauth2.user

abstract class OAuth2UserInfo(var attributes: Map<String, Any>) {
    abstract val id: String
    abstract val name: String?
    abstract val firstName: String?
    abstract val lastName: String?
    abstract val email: String?
    abstract val username: String
    abstract val imageUrl: String
}