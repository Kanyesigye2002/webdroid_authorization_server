package com.webdroid.webdroidauthorizationserver.security

import com.webdroid.webdroidauthorizationserver.config.AppProperties
import com.webdroid.webdroidauthorizationserver.entity.UserPrincipal
import io.jsonwebtoken.*
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.util.*

@Slf4j
@Service
@RequiredArgsConstructor
class TokenProvider @Autowired constructor(private val appConfig: AppProperties) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun createToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserPrincipal
        val now = Date()
        val expiryDate = Date(now.time + appConfig.auth.tokenExpirationMsec)
        return Jwts.builder()
            .setSubject(userPrincipal.id)
            .setIssuedAt(Date())
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, appConfig.auth.tokenSecret)
            .compact()
    }

    fun getUserIdFromToken(token: String?): String {
        val claims = Jwts.parser()
            .setSigningKey(appConfig.auth.tokenSecret)
            .parseClaimsJws(token)
            .body
        return claims.subject
    }

    fun validateToken(authToken: String?): Boolean {
        try {
            Jwts.parser().setSigningKey(appConfig.auth.tokenSecret).parseClaimsJws(authToken)
            return true
        } catch (ex: SignatureException) {
            logger.error("Invalid JWT signature")
        } catch (ex: MalformedJwtException) {
            logger.error("Invalid JWT token")
        } catch (ex: ExpiredJwtException) {
            logger.error("Expired JWT token")
        } catch (ex: UnsupportedJwtException) {
            logger.error("Unsupported JWT token")
        } catch (ex: IllegalArgumentException) {
            logger.error("JWT claims string is empty.")
        }
        return false
    }
}