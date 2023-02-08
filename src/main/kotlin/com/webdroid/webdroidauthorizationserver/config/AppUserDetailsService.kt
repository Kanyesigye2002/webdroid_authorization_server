package com.webdroid.webdroidauthorizationserver.config

import com.webdroid.webdroidauthorizationserver.config.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository
import com.webdroid.webdroidauthorizationserver.entity.PasswordResetToken
import com.webdroid.webdroidauthorizationserver.entity.Privilege
import com.webdroid.webdroidauthorizationserver.entity.Role
import com.webdroid.webdroidauthorizationserver.entity.UserPrincipal
import com.webdroid.webdroidauthorizationserver.exception.ResourceNotFoundException
import com.webdroid.webdroidauthorizationserver.repository.PasswordResetTokenRepository
import com.webdroid.webdroidauthorizationserver.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
class AppUserDetailsService @Autowired constructor(
    private val userRepository: UserRepository, private var passwordTokenRepository: PasswordResetTokenRepository
) : UserDetailsService {


    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(11)
    }

    @Bean
    fun cookieAuthorizationRequestRepository(): HttpCookieOAuth2AuthorizationRequestRepository {
        return HttpCookieOAuth2AuthorizationRequestRepository()
    }

    @Bean
    fun roleHierarchy(): RoleHierarchy {
        val roleHierarchy = RoleHierarchyImpl()
        val hierarchy = "ROLE_ADMIN > ROLE_STAFF \n ROLE_STAFF > ROLE_USER"
        roleHierarchy.setHierarchy(hierarchy)
        return roleHierarchy
    }

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository.findByUsername(email).orElseThrow { ResourceNotFoundException("User", "id", email) }
        return UserPrincipal.create(user)
    }

    fun loadUserById(id: String): UserDetails {
        val user = userRepository.findById(id).orElseThrow { ResourceNotFoundException("User", "id", id) }!!
        return UserPrincipal.create(user)
    }

    // UTIL
    private fun getAuthorities(roles: Collection<Role>): Collection<GrantedAuthority?> {
        return getGrantedAuthorities(getPrivileges(roles))
    }

    private fun getPrivileges(roles: Collection<Role>): List<String> {
        val privileges: MutableList<String> = ArrayList()
        val collection: MutableList<Privilege> = ArrayList()
        for (role in roles) {
            privileges.add(role.name!!)
            collection.addAll(role.privileges!!)
        }
        for (item in collection) {
            privileges.add(item.name!!)
        }
        return privileges
    }

    private fun getGrantedAuthorities(privileges: List<String>): List<GrantedAuthority?> {
        val authorities: MutableList<GrantedAuthority?> = ArrayList()
        for (privilege in privileges) {
            authorities.add(SimpleGrantedAuthority(privilege))
        }
        return authorities
    }

    fun validatePasswordResetToken(token: String?): String? {
        val passToken: PasswordResetToken = passwordTokenRepository.findByToken(token!!)
        return if (!isTokenFound(passToken)) "invalidToken" else if (isTokenExpired(passToken)) "expired" else null
    }

    private fun isTokenFound(passToken: PasswordResetToken?): Boolean {
        return passToken != null
    }

    private fun isTokenExpired(passToken: PasswordResetToken): Boolean {
        val cal = Calendar.getInstance()
        return passToken.expiryDate!!.before(cal.time)
    }
}