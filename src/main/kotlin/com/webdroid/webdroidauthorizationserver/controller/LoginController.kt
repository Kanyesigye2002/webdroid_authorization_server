package com.webdroid.webdroidauthorizationserver.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class LoginController {
    @GetMapping("/")
    fun Home(): String {
        return "index"
    }
    @GetMapping("/login")
    fun Login(): String {
        return "login"
    }
}