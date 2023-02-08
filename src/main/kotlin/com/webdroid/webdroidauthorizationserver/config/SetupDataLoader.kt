package com.webdroid.webdroidauthorizationserver.config

import com.webdroid.webdroidauthorizationserver.entity.Privilege
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
import java.util.*

@Component
class SetupDataLoader @Autowired constructor(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val privilegeRepository: PrivilegeRepository,
    private val passwordEncoder: PasswordEncoder
) : ApplicationListener<ContextRefreshedEvent> {
    private var alreadySetup = false

    // API
    @Transactional
    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        if (alreadySetup) {
            return
        }

        // == create initial privileges
        val readPrivilege = createPrivilegeIfNotFound("READ_PRIVILEGE")
        val writePrivilege = createPrivilegeIfNotFound("WRITE_PRIVILEGE")
        val passwordPrivilege = createPrivilegeIfNotFound("CHANGE_PASSWORD_PRIVILEGE")

        // == create initial roles
        val adminPrivileges: List<Privilege> =
            ArrayList(Arrays.asList(readPrivilege, writePrivilege, passwordPrivilege))
        val userPrivileges: List<Privilege> = ArrayList(Arrays.asList(readPrivilege, passwordPrivilege))
        val adminRole = createRoleIfNotFound("ROLE_ADMIN", adminPrivileges)
        createRoleIfNotFound("ROLE_USER", userPrivileges)

        // == create initial user
        createUserIfNotFound("test@test.com", "Test", "Test", "test", ArrayList(Arrays.asList(adminRole)))
        alreadySetup = true
    }

    @Transactional
    fun createPrivilegeIfNotFound(name: String): Privilege {
        var privilege = privilegeRepository.findByName(name)
        if (privilege == null) {
            privilege = Privilege(name)
            privilege = privilegeRepository.save(privilege)
        }
        return privilege
    }

    @Transactional
    fun createRoleIfNotFound(name: String, privileges: Collection<Privilege>?): Role {
        var role = roleRepository.findByName(name)
        if (role == null) {
            role = Role(name)
        }
        role.privileges = privileges
        role = roleRepository.save(role)
        return role
    }

    @Transactional
    fun createUserIfNotFound(
        email: String?,
        firstName: String?,
        lastName: String?,
        password: String?,
        roles: MutableList<Role>
    ): User {
        val optionalUser = userRepository.findByUsername(
            email!!
        )
        var user: User
        if (optionalUser.isEmpty) {
            user = User()
            user.firstName = firstName!!
            user.lastName = lastName!!
            user.password = passwordEncoder.encode(password)
            user.username = email
            user.email = email
            user.enabled = true
        } else {
            user = optionalUser.get()
        }
        user.roles = roles
        user = userRepository.save(user)
        return user
    }
}