package com.webdroid.webdroidauthorizationserver.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.*

@Entity
class DeviceMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
    var userId: String? = null
    var deviceDetails: String? = null
    var location: String? = null
    var lastLoggedIn: Date? = null
}