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
    var title: String? = null,
    var firstName: String? = null,
    var lastName: String? = null,
    var avatarUrl: String? = null,
    var phoneNumber: String? = null,
    var country: String? = null,
    var address: String? = null,
    var state: String? = null,
    var city: String? = null,
    @Column(columnDefinition = "TEXT")
    var about: String? = null,
    var providerId: String = "",
    var provider: AuthProvider = AuthProvider.local,
    var enabled: Boolean = true,

    //
    @OneToOne
    var notifications: UserNotifications? = null,
    @OneToOne
    var permissions: UserPermissions? = null,
    @ManyToOne
    var role: Role? = null,
)


