package com.webdroid.webdroidauthorizationserver.dto

class UserDto {
    var username: String? = null
    var firstName: String? = null
    var lastName: String? = null
    var password: String? = null
    var email: String? = null
    var isUsing2FA = false
    var role: Int? = null
}