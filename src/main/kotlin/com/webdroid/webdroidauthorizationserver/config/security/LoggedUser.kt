package com.webdroid.webdroidauthorizationserver.config.security

import jakarta.servlet.http.HttpSessionBindingEvent
import jakarta.servlet.http.HttpSessionBindingListener
import org.springframework.stereotype.Component
import java.io.Serializable

@Component
class LoggedUser : HttpSessionBindingListener, Serializable {
    final var username: String? = null
    private var activeUserStore: ActiveUserStore? = null

    constructor(username: String, activeUserStore: ActiveUserStore) {
        this.username = username
        this.activeUserStore = activeUserStore
    }

    constructor()

    override fun valueBound(event: HttpSessionBindingEvent) {
        val users: MutableList<String?> = activeUserStore!!.users
        val user = event.value as LoggedUser
        if (!users.contains(user.username)) {
            users.add(user.username)
        }
    }

    override fun valueUnbound(event: HttpSessionBindingEvent) {
        val users: MutableList<String?> = activeUserStore!!.users
        val user = event.value as LoggedUser
        users.remove(user.username)
    }

    companion object {
        private const val serialVersionUID = 1L
    }
}