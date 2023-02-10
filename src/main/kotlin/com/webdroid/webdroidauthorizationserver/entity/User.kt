package com.webdroid.webdroidauthorizationserver.entity


import jakarta.persistence.*
import lombok.Data
import org.hibernate.annotations.GenericGenerator


@Entity(name = "system_user")
@Data
class User(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    var id: String = "",
    var username: String = "",
    var password: String? = null,
    var email: String? = null,
    var name: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var avatarUrl: String? = null,
    var providerId: String = "",
    var provider: AuthProvider = AuthProvider.local,
    var isUsing2FA: Boolean = false,
    val secret: String? = null,
    var enabled: Boolean = false,
    //
    @ManyToMany(fetch = FetchType.EAGER) @JoinTable(name = "users_roles")
    var roles: MutableList<Role> = mutableListOf()
)


