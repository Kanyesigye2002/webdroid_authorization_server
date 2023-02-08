package com.webdroid.webdroidauthorizationserver.entity

import jakarta.persistence.*

@Entity
class NewLocationToken {
    //
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
    @JvmField
    var token: String? = null

    @JvmField
    @OneToOne(targetEntity = UserLocation::class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_location_id")
    var userLocation: UserLocation? = null

    //
    constructor() : super()

    constructor(token: String?, userLocation: UserLocation?) : super() {
        this.token = token
        this.userLocation = userLocation
    }
}