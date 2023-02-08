package com.webdroid.webdroidauthorizationserver.repository

import com.webdroid.webdroidauthorizationserver.entity.Privilege
import org.springframework.data.jpa.repository.JpaRepository

interface PrivilegeRepository : JpaRepository<Privilege, Long> {
    fun findByName(name: String): Privilege?
    override fun delete(privilege: Privilege)
}