package com.webdroid.webdroidauthorizationserver.entity

import jakarta.persistence.*

@Entity
class UserLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null
    @JvmField
    var country: String? = null
    var isEnabled: Boolean

    @ManyToOne(targetEntity = User::class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    var user: User? = null

    constructor() : super() {
        isEnabled = false
    }

    constructor(country: String?, user: User?) : super() {
        this.country = country
        this.user = user
        isEnabled = false
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (country == null) 0 else country.hashCode()
        result = prime * result + if (isEnabled) 1231 else 1237
        result = prime * result + if (id == null) 0 else id.hashCode()
        result = prime * result + if (user == null) 0 else user.hashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) {
            return true
        }
        if (obj == null) {
            return false
        }
        if (javaClass != obj.javaClass) {
            return false
        }
        val other = obj as UserLocation
        if (country == null) {
            if (other.country != null) {
                return false
            }
        } else if (country != other.country) {
            return false
        }
        if (isEnabled != other.isEnabled) {
            return false
        }
        if (id == null) {
            if (other.id != null) {
                return false
            }
        } else if (id != other.id) {
            return false
        }
        if (user == null) {
            if (other.user != null) {
                return false
            }
        } else if (user != other.user) {
            return false
        }
        return true
    }

    override fun toString(): String {
        return "UserLocation [id=" + id + ", country=" + country + ", enabled=" + isEnabled + ", user=" + user + "]"
    }
}