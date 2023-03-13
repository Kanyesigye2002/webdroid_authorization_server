package com.webdroid.webdroidauthorizationserver.controller

import com.webdroid.webdroidauthorizationserver.config.security.CurrentUser
import com.webdroid.webdroidauthorizationserver.config.security.TokenProvider
import com.webdroid.webdroidauthorizationserver.dto.UserDto
import com.webdroid.webdroidauthorizationserver.entity.User
import com.webdroid.webdroidauthorizationserver.entity.UserPrincipal
import com.webdroid.webdroidauthorizationserver.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/")
class AuthController @Autowired constructor(private val tokenProvider: TokenProvider, private val userService: UserService, private var authenticationProvider: AuthenticationProvider) {

    @GetMapping("account-info")
    fun getCurrentUser(): UserDetails {
        return UserDetails(userService.currentUser)
    }

    @GetMapping("profile")
    fun getCurrentUser(@CurrentUser userPrincipal: UserPrincipal): User? {
        return userService.getUserByID(userPrincipal.id).get()
    }

    @PostMapping("signup")
    fun registerUser(@RequestBody userDto: UserDto): LoginResponse {
        if (userService.exists(userDto.username!!)) {
            return LoginResponse(null, "User with email: " + userDto.username + " already exists", null)
        }
        val user = userService.registerNewUserAccount(userDto)
        return LoginResponse(tokenProvider.createToken(user), null, UserDetails(user))
    }

    @PostMapping("login")
    fun login(@RequestBody loginRequest: LoginRequest): LoginResponse {
        val credentials = UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
        return try {
            val securityContext = SecurityContextHolder.getContext()
            val authentication = authenticationProvider.authenticate(credentials)
            securityContext.authentication = authentication
            val user = userService.currentUser
            LoginResponse(tokenProvider.createToken(authentication), null, UserDetails(user))
        } catch (ex: AuthenticationException) {
            LoginResponse(null, "Incorrect password or email", null)
        }
    }

}