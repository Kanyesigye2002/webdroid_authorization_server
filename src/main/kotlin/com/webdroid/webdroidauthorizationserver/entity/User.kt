package com.webdroid.webdroidauthorizationserver.entity


import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
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
    var firstName: String = "",
    var lastName: String = "",
    var name: String = "",
    var avatarUrl: String = "",
    var providerId: String = "",
    var provider: AuthProvider = AuthProvider.local,
)


