package com.webdroid.webdroidauthorizationserver.security.oauth2.user

import com.webdroid.webdroidauthorizationserver.exception.OAuth2AuthenticationProcessingException
import com.webdroid.webdroidauthorizationserver.entity.AuthProvider

object OAuth2UserInfoFactory {
    fun getOAuth2UserInfo(registrationId: String, attributes: Map<String, Any>): OAuth2UserInfo {
        return if (registrationId.equals(
                AuthProvider.google.toString(),
                ignoreCase = true
            )
        ) {
            GoogleOAuth2UserInfo(attributes)
        } else {
            throw OAuth2AuthenticationProcessingException("Sorry! Login with $registrationId is not supported yet.")
        }
    }
}