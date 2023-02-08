package com.webdroid.webdroidauthorizationserver.config.security

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit

@Service
class LoginAttemptService {
    private val attemptsCache: LoadingCache<String, Int>

    @Autowired
    private val request: HttpServletRequest? = null

    init {
        attemptsCache =
            CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.DAYS).build(object : CacheLoader<String, Int>() {
                override fun load(key: String): Int {
                    return 0
                }
            })
    }

    fun loginFailed(key: String) {
        var attempts: Int
        attempts = try {
            attemptsCache[key]
        } catch (e: ExecutionException) {
            0
        }
        attempts++
        attemptsCache.put(key, attempts)
    }

    val isBlocked: Boolean
        get() = try {
            attemptsCache[clientIP] >= MAX_ATTEMPT
        } catch (e: ExecutionException) {
            false
        }
    private val clientIP: String
        private get() {
            val xfHeader = request!!.getHeader("X-Forwarded-For")
            return xfHeader?.split(",".toRegex())?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                ?.get(0) ?: request.remoteAddr
        }

    companion object {
        const val MAX_ATTEMPT = 10
    }
}