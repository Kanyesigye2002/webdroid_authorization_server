package com.webdroid.webdroidauthorizationserver.controller

import com.webdroid.webdroidauthorizationserver.entity.User

class SupportData

data class LoginResponse(
    val token: String?,
    val error: String?,
    val user: UserDetails?
)

data class UserDetails(
    val id: String,
    val avatarUrl: String?,
    val username: String,
    val firstName: String?,
    val lastName: String?,
    var name: String?,
    val email: String?,
) {
    constructor(user: User) : this(
        user.id,
        user.avatarUrl,
        user.username,
        user.firstName,
        user.lastName,
        user.name,
        user.email
    ) {
        if (user.name == null) {
            name = "${user.firstName} ${user.lastName}"
        }
    }
}

data class LoginRequest(val username: String, val password: String)
