package com.webdroid.webdroidauthorizationserver.config.security

class ActiveUserStore {
    @JvmField
    var users: MutableList<String?>

    init {
        users = ArrayList()
    }
}