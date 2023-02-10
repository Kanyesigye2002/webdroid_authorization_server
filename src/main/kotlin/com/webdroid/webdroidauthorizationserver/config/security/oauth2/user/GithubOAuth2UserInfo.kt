package com.webdroid.webdroidauthorizationserver.config.security.oauth2.user

class GithubOAuth2UserInfo(attributes: Map<String, Any>) : OAuth2UserInfo(attributes) {
    override val id: String
        get() = attributes["id"].toString()
    override val name: String?
        get() = attributes["name"] as String?
    override val firstName: String?
        get() = attributes["family_name"] as String?
    override val lastName: String?
        get() = attributes["given_name"] as String?
    override val email: String?
        get() = attributes["email"] as String?
    override val username: String
        get() = attributes["login"] as String
    override val imageUrl: String
        get() = attributes["avatar_url"] as String
}