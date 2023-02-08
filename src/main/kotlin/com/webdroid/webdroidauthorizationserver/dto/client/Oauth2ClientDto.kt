package com.webdroid.webdroidauthorizationserver.dto.client

import lombok.Builder
import lombok.Data
import java.io.Serializable
import java.util.*

@Data
@Builder
open class Oauth2ClientDto : Serializable {
    protected var id: String? = null
    protected var clientId: String? = null
    protected var clientName: String? = null
    protected var authorizationGrantTypes: String? = null
    protected var scopes: String? = null
    protected var createdDate: Date? = null
    protected var lastModifiedDate: Date? = null

    constructor()
    constructor(
        id: String?,
        clientId: String?,
        clientName: String?,
        authorizationGrantTypes: String?,
        scopes: String?,
        createdDate: Date?,
        lastModifiedDate: Date?
    ) {
        this.id = id
        this.clientId = clientId
        this.clientName = clientName
        this.authorizationGrantTypes = authorizationGrantTypes
        this.scopes = scopes
        this.createdDate = createdDate
        this.lastModifiedDate = lastModifiedDate
    }

    companion object {
        private const val serialVersionUID = 4687988559162263447L
    }
}