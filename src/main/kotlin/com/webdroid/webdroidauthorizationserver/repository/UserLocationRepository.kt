package com.webdroid.webdroidauthorizationserver.repository

import com.webdroid.webdroidauthorizationserver.entity.User
import com.webdroid.webdroidauthorizationserver.entity.UserLocation
import org.springframework.data.jpa.repository.JpaRepository

interface UserLocationRepository : JpaRepository<UserLocation, Long> {
    fun findByCountryAndUser(country: String, user: User): UserLocation
}