package com.webdroid.webdroidauthorizationserver.repository

import com.webdroid.webdroidauthorizationserver.entity.Oauth2Client
import org.springframework.data.jpa.repository.JpaRepository

interface Oauth2ClientRepository : JpaRepository<Oauth2Client, String>