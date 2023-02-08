package com.webdroid.webdroidauthorizationserver.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

class UserDto {
    var firstName: @NotNull @Size(min = 1, message = "{Size.userDto.firstName}") String? = null
    var lastName: @NotNull @Size(min = 1, message = "{Size.userDto.lastName}") String? = null

    var password: String? = null
    var matchingPassword: @NotNull @Size(min = 1) String? = null


    var email: @NotNull @Size(min = 1, message = "{Size.userDto.email}") String? = null
    var isUsing2FA = false
    var role: Int? = null
    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("UserDto [firstName=")
            .append(firstName)
            .append(", lastName=")
            .append(lastName)
            .append(", email=")
            .append(email)
            .append(", isUsing2FA=")
            .append(isUsing2FA)
            .append(", role=")
            .append(role).append("]")
        return builder.toString()
    }
}