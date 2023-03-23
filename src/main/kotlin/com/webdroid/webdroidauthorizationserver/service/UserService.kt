package com.webdroid.webdroidauthorizationserver.service

import com.webdroid.webdroidauthorizationserver.dto.UserDto
import com.webdroid.webdroidauthorizationserver.entity.*
import com.webdroid.webdroidauthorizationserver.exception.UserAlreadyExistsException
import com.webdroid.webdroidauthorizationserver.repository.*
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
@Transactional
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val tokenRepository: VerificationTokenRepository,
    private val passwordTokenRepository: PasswordResetTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val roleRepository: RoleRepository,
    private val userLocationRepository: UserLocationRepository,
    private val newLocationTokenRepository: NewLocationTokenRepository,
    private val env: Environment
) {


    fun exists(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }

    val currentUser: User
        get() = Optional
            .ofNullable(SecurityContextHolder.getContext())
            .map { obj: SecurityContext -> obj.authentication }
            .map { obj: Authentication -> obj.name }
            .flatMap { userName: String -> userRepository.findByUsername(userName) }
            .orElse(null)
    
    // API
    fun registerNewUserAccount(accountDto: UserDto): User {
        if (exists(accountDto.username!!)) {
            throw UserAlreadyExistsException("There is an account with that email address: " + accountDto.email)
        }
        val user = User()
        user.firstName = accountDto.firstName!!
        user.lastName = accountDto.lastName!!
        user.password = passwordEncoder.encode(accountDto.password)
        user.email = accountDto.email
        user.username = accountDto.email!!
        user.isUsing2FA = accountDto.isUsing2FA
        user.roles = mutableListOf(roleRepository.findByName("ROLE_USER")!!)
        return userRepository.save(user)
    }

    fun getUser(verificationToken: String?): User? {
        val token = tokenRepository.findByToken(verificationToken!!)
        return token.user
    }

    fun getVerificationToken(verificationToken: String?): VerificationToken? {
        return tokenRepository.findByToken(verificationToken!!)
    }

    fun saveRegisteredUser(user: User) {
        userRepository.save(user)
    }

    fun deleteUser(user: User?) {
        val verificationToken = tokenRepository.findByUser(user!!)
        tokenRepository.delete(verificationToken)
        val passwordToken = passwordTokenRepository.findByUser(user)
        passwordTokenRepository.delete(passwordToken)
        userRepository.delete(user)
    }

    fun createVerificationTokenForUser(user: User?, token: String?) {
        val myToken = VerificationToken(token, user)
        tokenRepository.save(myToken)
    }

    fun generateNewVerificationToken(token: String?): VerificationToken? {
        var vToken = tokenRepository.findByToken(token!!)
        vToken.updateToken(
            UUID.randomUUID()
                .toString()
        )
        vToken = tokenRepository.save(vToken)
        return vToken
    }

    fun createPasswordResetTokenForUser(user: User?, token: String?) {
        val myToken = PasswordResetToken(token, user)
        passwordTokenRepository.save(myToken)
    }

    fun findUserByEmail(email: String?): User? {
        return userRepository.findByUsername(email!!).get()
    }

    fun getPasswordResetToken(token: String?): PasswordResetToken? {
        return passwordTokenRepository.findByToken(token!!)
    }

    fun getUserByPasswordResetToken(token: String?): Optional<User> {
        return Optional.ofNullable(passwordTokenRepository.findByToken(token!!).user)
    }

    fun getUserByID(id: String?): Optional<User?> {
        return userRepository.findById(id!!)
    }

    fun changeUserPassword(user: User?, password: String?) {
        user!!.password = passwordEncoder.encode(password)
        userRepository.save(user)
    }

    fun checkIfValidOldPassword(user: User?, password: String?): Boolean {
        return passwordEncoder.matches(password, user!!.password)
    }

    fun validateVerificationToken(token: String?): String? {
        val verificationToken = tokenRepository.findByToken(token!!)
        val user = verificationToken.user
        val cal = Calendar.getInstance()
        if (verificationToken.expiryDate!!
                .time - cal.time
                .time <= 0
        ) {
            tokenRepository.delete(verificationToken)
            return TOKEN_EXPIRED
        }
        user!!.enabled = true
        // tokenRepository.delete(verificationToken);
        userRepository.save(user)
        return TOKEN_VALID
    }

    private val isGeoIpLibEnabled: Boolean
        get() = java.lang.Boolean.parseBoolean(env.getProperty("geo.ip.lib.enabled"))

    private fun createNewLocationToken(country: String, user: User): NewLocationToken {
        var loc = UserLocation(country, user)
        loc = userLocationRepository.save(loc)
        val token = NewLocationToken(
            UUID.randomUUID()
                .toString(), loc
        )
        return newLocationTokenRepository.save(token)
    }

    companion object {
        const val TOKEN_INVALID = "invalidToken"
        const val TOKEN_EXPIRED = "expired"
        const val TOKEN_VALID = "valid"
    }
}