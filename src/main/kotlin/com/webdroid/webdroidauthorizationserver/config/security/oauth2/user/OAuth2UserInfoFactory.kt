package com.webdroid.webdroidauthorizationserver.config.security.oauth2.user

import com.webdroid.webdroidauthorizationserver.entity.AuthProvider
import com.webdroid.webdroidauthorizationserver.exception.OAuth2AuthenticationProcessingException

object OAuth2UserInfoFactory {
    fun getOAuth2UserInfo(registrationId: String, attributes: Map<String, Any>): OAuth2UserInfo {
        return if (registrationId.equals(
                AuthProvider.google.toString(),
                ignoreCase = true
            )
        ) {
            GoogleOAuth2UserInfo(attributes)
        } else if (registrationId.equals(
                AuthProvider.github.toString(),
                ignoreCase = true
            )
        ) {
            GithubOAuth2UserInfo(attributes)
        } else {
            throw OAuth2AuthenticationProcessingException("Sorry! Login with $registrationId is not supported yet.")
        }
    }
}