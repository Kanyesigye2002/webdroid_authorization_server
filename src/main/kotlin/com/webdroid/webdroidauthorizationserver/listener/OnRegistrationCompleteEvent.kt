package com.webdroid.webdroidauthorizationserver.listener

import com.webdroid.webdroidauthorizationserver.entity.User
import org.springframework.context.ApplicationEvent

class OnRegistrationCompleteEvent(
    @JvmField val user: User,
    @JvmField val appUrl: String?
) : ApplicationEvent(user)