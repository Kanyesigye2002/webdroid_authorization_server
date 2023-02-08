package com.webdroid.webdroidauthorizationserver.repository

import com.webdroid.webdroidauthorizationserver.entity.DeviceMetadata
import org.springframework.data.jpa.repository.JpaRepository

interface DeviceMetadataRepository : JpaRepository<DeviceMetadata, Long> {
    fun findByUserId(userId: String): List<DeviceMetadata>
}