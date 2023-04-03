package com.webdroid.webdroidauthorizationserver.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class UserPermissions {
    //
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null
    val staff_read: Boolean = false
    val staff_write: Boolean = false
    val properties_read: Boolean = false
    val properties_write: Boolean = false
    val hardware_read: Boolean = false
    val hardware_write: Boolean = false
    val auction_read: Boolean = false
    val auction_write: Boolean = false
    val crowd_read: Boolean = false
    val crowd_write: Boolean = false
    val transactions_read: Boolean = false
    val transactions_write: Boolean = false
    val accounting_read: Boolean = false
}