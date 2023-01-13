package com.webdroid.webdroidauthorizationserver.service

import com.webdroid.webdroidauthorizationserver.entity.UserPrincipal
import com.webdroid.webdroidauthorizationserver.exception.ResourceNotFoundException
import com.webdroid.webdroidauthorizationserver.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
@Transactional
class CustomUserDetailsService : UserDetailsService {
    @Autowired
    private val userRepository: UserRepository? = null
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(11)
    }

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        val user = userRepository!!.findByUsername(email).orElseThrow { ResourceNotFoundException("User", "id", email) }
        return UserPrincipal.create(user)
    }

    fun loadUserById(id: String): UserDetails {
        val user = userRepository!!.findById(id).orElseThrow { ResourceNotFoundException("User", "id", id) }!!
        return UserPrincipal.create(user)
    }

    private fun getAuthorities(roles: List<String>): Collection<GrantedAuthority> {
        val authorities: MutableList<GrantedAuthority> = ArrayList()
        for (role in roles) {
            authorities.add(SimpleGrantedAuthority(role))
        }
        return authorities
    }
}