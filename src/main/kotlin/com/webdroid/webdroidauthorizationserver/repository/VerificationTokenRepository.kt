package com.webdroid.webdroidauthorizationserver.repository

import com.webdroid.webdroidauthorizationserver.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.util.*
import java.util.stream.Stream

interface VerificationTokenRepository : JpaRepository<VerificationToken, Long> {
    fun findByToken(token: String): VerificationToken
    fun findByUser(user: User): VerificationToken
    fun findAllByExpiryDateLessThan(now: Date): Stream<VerificationToken>
    fun deleteByExpiryDateLessThan(now: Date)

    @Modifying
    @Query("delete from VerificationToken t where t.expiryDate <= ?1")
    fun deleteAllExpiredSince(now: Date)
}