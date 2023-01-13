package com.webdroid.webdroidauthorizationserver.security.oauth2.user

class GoogleOAuth2UserInfo(attributes: Map<String, Any>) : OAuth2UserInfo(attributes) {
    override val id: String
        get() = attributes["sub"] as String
    override val name: String
        get() = attributes["name"] as String
    override val firstName: String
        get() = attributes["family_name"] as String
    override val lastName: String
        get() = attributes["given_name"] as String
    override val email: String
        get() = attributes["email"] as String
    override val imageUrl: String
        get() = attributes["picture"] as String
}