package com.webdroid.webdroidauthorizationserver.entity

import jakarta.persistence.*

@Entity
class Role {
    //
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null

    @ManyToMany(mappedBy = "roles")
    var users: Collection<User>? = null

    @ManyToMany
    @JoinTable(
        name = "roles_privileges",
        joinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "privilege_id", referencedColumnName = "id")]
    )
    var privileges: Collection<Privilege>? = null
    var name: String? = null

    constructor() : super()
    constructor(name: String?) : super() {
        this.name = name
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (name == null) 0 else name.hashCode()
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
        val role = obj as Role
        return if (name != role.name) {
            false
        } else true
    }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("Role [name=").append(name).append("]").append("[id=").append(id).append("]")
        return builder.toString()
    }
}