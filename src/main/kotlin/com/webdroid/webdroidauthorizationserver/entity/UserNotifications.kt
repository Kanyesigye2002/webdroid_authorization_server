package com.webdroid.webdroidauthorizationserver.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class UserNotifications {
    //
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null
    var new_property: Boolean = false
    var property_rejected: Boolean = false
    var property_approved: Boolean = false
    var new_auction: Boolean = false
    var auction_edited: Boolean = false
    var new_staff: Boolean = false
    var delete_staff: Boolean = false
    var edit_staff: Boolean = false
    var monthly_report: Boolean = false
    var yearly_report: Boolean = false
}