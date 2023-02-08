package com.webdroid.webdroidauthorizationserver.service

import com.google.common.base.Strings
import com.maxmind.geoip2.DatabaseReader
import com.maxmind.geoip2.exception.GeoIp2Exception
import com.webdroid.webdroidauthorizationserver.entity.DeviceMetadata
import com.webdroid.webdroidauthorizationserver.entity.User
import com.webdroid.webdroidauthorizationserver.repository.DeviceMetadataRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component
import ua_parser.Parser
import java.io.IOException
import java.net.InetAddress
import java.util.*

@Component
class DeviceService @Autowired constructor(
    private val deviceMetadataRepository: DeviceMetadataRepository,
    private val parser: Parser,
    private val mailSender: JavaMailSender,
    private val messages: MessageSource
){
    @Value("\${support.email}")
    private val from: String? = null

    @Autowired
    @Qualifier("GeoIPCity")
    private val databaseReader: DatabaseReader? = null

    @Throws(IOException::class, GeoIp2Exception::class)
    fun verifyDevice(user: User, request: HttpServletRequest) {
        val ip = extractIp(request)
        val location = getIpLocation(ip)
        val deviceDetails = getDeviceDetails(request.getHeader("user-agent"))
        val existingDevice = findExistingDevice(user.id, deviceDetails, location)
        if (Objects.isNull(existingDevice)) {
            unknownDeviceNotification(deviceDetails, location, ip, user.email)
            val deviceMetadata = DeviceMetadata()
            deviceMetadata.userId = user.id
            deviceMetadata.location = location
            deviceMetadata.deviceDetails = deviceDetails
            deviceMetadata.lastLoggedIn = Date()
            deviceMetadataRepository.save(deviceMetadata)
        } else {
            existingDevice!!.lastLoggedIn = Date()
            deviceMetadataRepository.save(existingDevice)
        }
    }

    private fun extractIp(request: HttpServletRequest): String {
        val clientIp: String
        val clientXForwardedForIp = request.getHeader("x-forwarded-for")
        clientIp = if (Objects.nonNull(clientXForwardedForIp)) {
            parseXForwardedHeader(clientXForwardedForIp)
        } else {
            request.remoteAddr
        }
        return clientIp
    }

    private fun parseXForwardedHeader(header: String): String {
        return header.split(" *, *".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
    }

    private fun getDeviceDetails(userAgent: String): String {
        var deviceDetails = UNKNOWN
        val client = parser.parse(userAgent)
        if (Objects.nonNull(client)) {
            deviceDetails = client.userAgent.family + " " + client.userAgent.major + "." + client.userAgent.minor +
                    " - " + client.os.family + " " + client.os.major + "." + client.os.minor
        }
        return deviceDetails
    }

    @Throws(IOException::class, GeoIp2Exception::class)
    private fun getIpLocation(ip: String): String {
        var location = UNKNOWN
        val ipAddress = InetAddress.getByName(ip)
        val cityResponse = databaseReader!!.city(ipAddress)
        if (Objects.nonNull(cityResponse) &&
            Objects.nonNull(cityResponse.city) &&
            !Strings.isNullOrEmpty(cityResponse.city.name)
        ) {
            location = cityResponse.city.name
        }
        return location
    }

    private fun findExistingDevice(userId: String, deviceDetails: String, location: String): DeviceMetadata? {
        val knownDevices = deviceMetadataRepository.findByUserId(userId)
        for (existingDevice in knownDevices) {
            if (existingDevice.deviceDetails == deviceDetails && existingDevice.location == location) {
                return existingDevice
            }
        }
        return null
    }

    private fun unknownDeviceNotification(
        deviceDetails: String,
        location: String,
        ip: String,
        email: String?
    ) {
        val subject = "New Login Notification"
        val notification = SimpleMailMessage()
        notification.setTo(email)
        notification.subject = subject
        val text = """Device details: $deviceDetails
Location: $location
IP Address: $ip"""
        notification.text = text
        notification.from = from
        mailSender.send(notification)
    }

    companion object {
        private const val UNKNOWN = "UNKNOWN"
    }
}