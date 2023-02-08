package com.webdroid.webdroidauthorizationserver.service.client

import com.webdroid.webdroidauthorizationserver.dto.client.ViewOauth2ClientDto

interface Oauth2ClientService {
    fun find(): List<ViewOauth2ClientDto?>?
    fun delete(ids: List<String?>?)
}