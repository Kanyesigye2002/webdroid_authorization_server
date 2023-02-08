package com.webdroid.webdroidauthorizationserver.task

import com.webdroid.webdroidauthorizationserver.repository.PasswordResetTokenRepository
import com.webdroid.webdroidauthorizationserver.repository.VerificationTokenRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
@Transactional
class TokensPurgeTask @Autowired constructor(var tokenRepository: VerificationTokenRepository, var passwordTokenRepository: PasswordResetTokenRepository) {

    @Scheduled(cron = "\${purge.cron.expression}")
    fun purgeExpired() {
        val now = Date.from(Instant.now())
        passwordTokenRepository.deleteAllExpiredSince(now)
        tokenRepository.deleteAllExpiredSince(now)
    }
}