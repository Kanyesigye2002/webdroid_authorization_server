package com.webdroid.webdroidauthorizationserver.listener

import com.webdroid.webdroidauthorizationserver.entity.User
import com.webdroid.webdroidauthorizationserver.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.core.env.Environment
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import java.util.*

@Component
class RegistrationListener @Autowired constructor(
    private val service: UserService,
    private val mailSender: JavaMailSender,
    private val env: Environment
) : ApplicationListener<OnRegistrationCompleteEvent> {

    // API
    override fun onApplicationEvent(event: OnRegistrationCompleteEvent) {
        confirmRegistration(event)
    }

    private fun confirmRegistration(event: OnRegistrationCompleteEvent) {
        val user = event.user
        val token = UUID.randomUUID().toString()
        service.createVerificationTokenForUser(user, token)
        val email = constructEmailMessage(event, user, token)
        mailSender.send(email)
    }

    //
    private fun constructEmailMessage(
        event: OnRegistrationCompleteEvent,
        user: User,
        token: String
    ): SimpleMailMessage {
        val recipientAddress = user.email
        val subject = "Registration Confirmation"
        val confirmationUrl = event.appUrl + "/registration-confirm?token=" + token
        val message = "You registered successfully. To confirm your registration, please click on the below link."
        val email = SimpleMailMessage()
        email.setTo(recipientAddress)
        email.subject = subject
        email.text = "$message \r\n$confirmationUrl"
        email.from = env.getProperty("support.email")
        return email
    }
}