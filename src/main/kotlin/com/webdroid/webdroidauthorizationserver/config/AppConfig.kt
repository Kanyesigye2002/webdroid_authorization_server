package com.webdroid.webdroidauthorizationserver.config

import com.webdroid.webdroidauthorizationserver.config.security.ActiveUserStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl

@Configuration
class AppConfig {
    // beans
    @Bean
    fun activeUserStore(): ActiveUserStore {
        return ActiveUserStore()
    }

    @Bean
    fun customRoleHierarchy(): RoleHierarchy? {
        val roleHierarchy = RoleHierarchyImpl()
        val hierarchy = "ROLE_ADMIN > ROLE_STAFF \n ROLE_STAFF > ROLE_USER"
        roleHierarchy.setHierarchy(hierarchy)
        return roleHierarchy
    }

}