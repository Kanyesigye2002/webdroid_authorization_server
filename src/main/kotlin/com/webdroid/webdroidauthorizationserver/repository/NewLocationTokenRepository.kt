package com.webdroid.webdroidauthorizationserver.repository

import com.webdroid.webdroidauthorizationserver.entity.NewLocationToken
import com.webdroid.webdroidauthorizationserver.entity.UserLocation
import org.springframework.data.jpa.repository.JpaRepository

interface NewLocationTokenRepository : JpaRepository<NewLocationToken, Long> {
    fun findByToken(token: String): NewLocationToken
    fun findByUserLocation(userLocation: UserLocation): NewLocationToken?
}