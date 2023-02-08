package com.webdroid.webdroidauthorizationserver.config.security

import com.webdroid.webdroidauthorizationserver.config.AppProperties
import com.webdroid.webdroidauthorizationserver.entity.UserPrincipal
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.InvalidKeyException
import io.jsonwebtoken.security.Keys
import lombok.extern.slf4j.Slf4j
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey


@Slf4j
@Service
class TokenProvider @Autowired constructor(private val appConfig: AppProperties) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun createToken(authentication: Authentication): String {
        val userPrincipal = authentication.principal as UserPrincipal
        val now = Date()
        val expiryDate = Date(now.time + appConfig.auth.tokenExpirationMsec)
        val key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(appConfig.auth.tokenSecret))
        return Jwts.builder()
            .setId(UUID.randomUUID().toString())
            .setIssuer("Webdroid")
            .setSubject(userPrincipal.id)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }

    fun getUserIdFromToken(token: String?): String {

        val key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(appConfig.auth.tokenSecret))
val claims = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
        return claims.subject
    }

    fun validateToken(authToken: String?): Boolean {
        try {
            val key: SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(appConfig.auth.tokenSecret))
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(authToken)
            return true
        } catch (ex: InvalidKeyException) {
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
