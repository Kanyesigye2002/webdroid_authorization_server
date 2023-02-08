package com.webdroid.webdroidauthorizationserver.repository

import com.webdroid.webdroidauthorizationserver.entity.Role
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<Role?, Long?> {
    fun findByName(name: String): Role?
    override fun delete(role: Role)
}