package com.webdroid.webdroidauthorizationserver.controller

import com.webdroid.webdroidauthorizationserver.config.AppProperties
import com.webdroid.webdroidauthorizationserver.config.AppUserDetailsService
import com.webdroid.webdroidauthorizationserver.config.security.ActiveUserStore
import com.webdroid.webdroidauthorizationserver.dto.PasswordDto
import com.webdroid.webdroidauthorizationserver.dto.UserDto
import com.webdroid.webdroidauthorizationserver.entity.User
import com.webdroid.webdroidauthorizationserver.entity.VerificationToken
import com.webdroid.webdroidauthorizationserver.exception.InvalidOldPasswordException
import com.webdroid.webdroidauthorizationserver.listener.OnRegistrationCompleteEvent
import com.webdroid.webdroidauthorizationserver.service.UserService
import com.webdroid.webdroidauthorizationserver.util.GenericResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher
import org.springframework.core.env.Environment
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class RegistrationRestController @Autowired constructor(
    private val userService: UserService,
    private val securityUserService: AppUserDetailsService,
    private val mailSender: JavaMailSender,
    private val eventPublisher: ApplicationEventPublisher,
    private val env: Environment,
    private val appConfig: AppProperties,
    private var activeUserStore: ActiveUserStore
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    // Registration
    @PostMapping("/user/registration")
    fun registerUserAccount(accountDto: @Valid UserDto?, request: HttpServletRequest): GenericResponse {
        logger.debug("Registering user account with information: {}", accountDto)
        val registered = userService.registerNewUserAccount(accountDto)
        userService.addUserLocation(registered, getClientIP(request))
        eventPublisher.publishEvent(OnRegistrationCompleteEvent(registered!!, appConfig.frontendUrl))
        return GenericResponse("success")
    }

    // Registration
    @RequestMapping("/redirect")
    fun redirect(response: HttpServletResponse) {
        response.sendRedirect("foobar://success?code=1337")
    }

    // User activation - verification
    @GetMapping("/user/resendRegistrationToken")
    fun resendRegistrationToken(
        request: HttpServletRequest,
        @RequestParam("token") existingToken: String?
    ): GenericResponse {
        val newToken = userService.generateNewVerificationToken(existingToken)
        val user = userService.getUser(newToken!!.token)
        mailSender.send(constructResendVerificationTokenEmail(appConfig.frontendUrl, newToken, user))
        return GenericResponse("We will send an email with a new registration token to your email account")
    }

    // Reset password
    @PostMapping("/user/resetPassword")
    fun resetPassword(@RequestParam("email") userEmail: String?): GenericResponse {
        val user = userService.findUserByEmail(userEmail)
        if (user != null) {
            val token = UUID.randomUUID().toString()
            userService.createPasswordResetTokenForUser(user, token)
            mailSender.send(constructResetTokenEmail(appConfig.frontendUrl, token, user))
        }
        return GenericResponse("You should receive an Password Reset Email shortly")
    }

    // Save password
    @PostMapping("/user/savePassword")
    fun savePassword(passwordDto: @Valid PasswordDto): GenericResponse {
        val result = securityUserService.validatePasswordResetToken(passwordDto.token)
        if (result != null) {
            return GenericResponse("Invalid token.")
        }
        val user = userService.getUserByPasswordResetToken(
            passwordDto.token
        )
        return if (user.isPresent) {
            userService.changeUserPassword(user.get(), passwordDto.newPassword)
            GenericResponse("Password reset successfully")
        } else {
            GenericResponse("Invalid user")
        }
    }

    // Change user password
    @PostMapping("/user/update-password")
    fun changeUserPassword(passwordDto: @Valid PasswordDto): GenericResponse {
        val user =
            userService.findUserByEmail((SecurityContextHolder.getContext().authentication.principal as User).username)
        if (!userService.checkIfValidOldPassword(user, passwordDto.oldPassword)) {
            throw InvalidOldPasswordException()
        }
        userService.changeUserPassword(user, passwordDto.newPassword)
        return GenericResponse("Password updated successfully")
    }

    @GetMapping("/loggedUsers")
    fun getLoggedUsers(): MutableList<String?> {
        return activeUserStore.users
    }

    // ============== NON-API ============
    private fun constructResendVerificationTokenEmail(
        contextPath: String?,
        newToken: VerificationToken?,
        user: User?
    ): SimpleMailMessage {
        val confirmationUrl = contextPath + "/registration-confirm?token=" + newToken!!.token
        val message = "We will send an email with a new registration token to your email account"
        return constructEmail("Resend Registration Token", "$message \r\n$confirmationUrl", user)
    }

    private fun constructResetTokenEmail(
        contextPath: String?,
        token: String,
        user: User
    ): SimpleMailMessage {
        val url = "$contextPath/user/change-password?token=$token"
        val message = "Reset Password"
        return constructEmail("Reset Password", "$message \r\n$url", user)
    }

    private fun constructEmail(subject: String, body: String, user: User?): SimpleMailMessage {
        val email = SimpleMailMessage()
        email.subject = subject
        email.text = body
        email.setTo(user!!.email)
        email.from = env.getProperty("support.email")
        return email
    }

    private fun getAppUrl(request: HttpServletRequest): String {
        return "http://" + request.serverName + ":" + request.serverPort + request.contextPath
    }

    private fun getClientIP(request: HttpServletRequest): String {
        val xfHeader = request.getHeader("X-Forwarded-For")
        return if (xfHeader == null || xfHeader.isEmpty() || !xfHeader.contains(request.remoteAddr)) {
            request.remoteAddr
        } else xfHeader.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
    }
}