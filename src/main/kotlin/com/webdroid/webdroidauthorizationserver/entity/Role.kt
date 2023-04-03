package com.webdroid.webdroidauthorizationserver.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
    var name: String? = null

    constructor() : super()
    constructor(name: String?) : super() {
        this.name = name
    }
}