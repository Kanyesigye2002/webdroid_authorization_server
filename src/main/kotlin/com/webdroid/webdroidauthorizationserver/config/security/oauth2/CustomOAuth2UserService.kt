package com.webdroid.webdroidauthorizationserver.config.security.oauth2


import com.webdroid.webdroidauthorizationserver.config.security.oauth2.user.OAuth2UserInfo
import com.webdroid.webdroidauthorizationserver.config.security.oauth2.user.OAuth2UserInfoFactory
import com.webdroid.webdroidauthorizationserver.entity.AuthProvider
import com.webdroid.webdroidauthorizationserver.entity.User
import com.webdroid.webdroidauthorizationserver.entity.UserPrincipal
import com.webdroid.webdroidauthorizationserver.exception.OAuth2AuthenticationProcessingException
import com.webdroid.webdroidauthorizationserver.repository.UserRepository
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.InternalAuthenticationServiceException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

@Service
@RequiredArgsConstructor
class CustomOAuth2UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : DefaultOAuth2UserService() {
    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(oAuth2UserRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(oAuth2UserRequest)
        return try {
            processOAuth2User(oAuth2UserRequest, oAuth2User)
        } catch (ex: AuthenticationException) {
            throw ex
        } catch (ex: Exception) {
            throw InternalAuthenticationServiceException(ex.message, ex.cause)
        }
    }

    private fun processOAuth2User(oAuth2UserRequest: OAuth2UserRequest, oAuth2User: OAuth2User): OAuth2User {
        val oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
            oAuth2UserRequest.clientRegistration.registrationId,
            oAuth2User.attributes
        )
        if (!StringUtils.hasText(oAuth2UserInfo.username)) {
            throw OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider")
        }
        val userOptional = userRepository.findByUsername(oAuth2UserInfo.username)
        var user: User
        if (userOptional.isPresent) {
            user = userOptional.get()
            if (user.provider != AuthProvider.valueOf(oAuth2UserRequest.clientRegistration.registrationId)) {
                throw OAuth2AuthenticationProcessingException(
                    "Looks like you're signed up with " +
                            user.provider + " account. Please use your " + user.provider +
                            " account to login."
                )
            }
            user = updateExistingUser(user, oAuth2UserInfo)
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo)
        }
        return UserPrincipal.create(user, oAuth2User.attributes)
    }

    private fun registerNewUser(oAuth2UserRequest: OAuth2UserRequest, oAuth2UserInfo: OAuth2UserInfo): User {
        val user = User()
        user.provider = AuthProvider.valueOf(oAuth2UserRequest.clientRegistration.registrationId)
        user.providerId = oAuth2UserInfo.id
        if (oAuth2UserInfo.attributes["name"] != null) {
            user.name = oAuth2UserInfo.name
        } else {
            user.name = oAuth2UserInfo.username
        }
        user.firstName = oAuth2UserInfo.firstName
        user.lastName = oAuth2UserInfo.lastName
        user.username = oAuth2UserInfo.username
        if (oAuth2UserRequest.clientRegistration.registrationId == "github") {
            if (oAuth2UserInfo.attributes["email"] != null) {
                user.email = oAuth2UserInfo.email
            }
        } else {
            user.email = oAuth2UserInfo.email
        }
        user.avatarUrl = oAuth2UserInfo.imageUrl
        return userRepository.save(user)
    }

    private fun updateExistingUser(existingUser: User, oAuth2UserInfo: OAuth2UserInfo): User {
        existingUser.name = oAuth2UserInfo.name
        existingUser.firstName = oAuth2UserInfo.firstName
        existingUser.lastName = oAuth2UserInfo.lastName
        existingUser.avatarUrl = oAuth2UserInfo.imageUrl
        return userRepository.save(existingUser)
    }
}