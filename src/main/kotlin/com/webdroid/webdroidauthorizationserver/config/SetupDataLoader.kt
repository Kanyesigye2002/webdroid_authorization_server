package com.webdroid.webdroidauthorizationserver.config

import com.webdroid.webdroidauthorizationserver.entity.Role
import com.webdroid.webdroidauthorizationserver.entity.User
import com.webdroid.webdroidauthorizationserver.repository.PrivilegeRepository
import com.webdroid.webdroidauthorizationserver.repository.RoleRepository
import com.webdroid.webdroidauthorizationserver.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class SetupDataLoader @Autowired constructor(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val privilegeRepository: PrivilegeRepository,
    private val passwordEncoder: PasswordEncoder
) : ApplicationListener<ContextRefreshedEvent> {
    private var alreadySetup = false

    @Transactional
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        if (alreadySetup) {
            return
        }

        val adminRole = createRoleIfNotFound("ROLE_ADMIN")
        val userRole = createRoleIfNotFound("ROLE_USER")

        createUserIfNotFound("test@test.com", "Test", "Account", "", "0700000000", "https://api-dev-minimal-v4.vercel.app/assets/images/covers/avatar_1.jpg", "test", userRole)
        createUserIfNotFound("test@webdroid.com", "Webdroid", "Services", "Programmer", "0750000000","https://api-dev-minimal-v4.vercel.app/assets/images/covers/avatar_2.jpg","webdroid", adminRole)
        alreadySetup = true
    }

    @Transactional
    fun createRoleIfNotFound(name: String): Role {
        var role = roleRepository.findByName(name)
        if (role == null) {
            role = Role(name)
        }
        role = roleRepository.save(role)
        return role
    }

    @Transactional
    fun createUserIfNotFound(
        email: String?,
        firstName: String?,
        lastName: String?,
        title: String?,
        phoneNumber: String?,
        avatarUrl: String?,
        password: String?,
        role: Role
    ): User {
        val optionalUser = userRepository.findByUsername(
            email!!
        )
        var user: User
        if (optionalUser.isEmpty) {
            user = User()
            user.firstName = firstName!!
            user.lastName = lastName!!
            user.name = "$firstName $lastName"
            user.title = title
            user.phoneNumber = phoneNumber
            user.avatarUrl = avatarUrl
            user.city = "Kampala"
            user.country = "Uganda"
            user.password = passwordEncoder.encode(password)
            user.username = email
            user.email = email
            user.enabled = true
        } else {
            user = optionalUser.get()
        }
        user.role = role
        user = userRepository.save(user)
        return user
    }
}