package com.webdroid.webdroidauthorizationserver.entity

import jakarta.persistence.*

@Entity
class Privilege {
    //
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
    var name: String? = null

    @ManyToMany(mappedBy = "privileges")
    var roles: Collection<Role>? = null

    constructor() : super()
    constructor(name: String?) : super() {
        this.name = name
    }

}