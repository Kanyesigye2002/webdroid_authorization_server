package com.webdroid.webdroidauthorizationserver.dto.client

import lombok.Builder
import lombok.Data
import lombok.EqualsAndHashCode
import java.util.*

@Data
@EqualsAndHashCode(callSuper = true)
class ViewOauth2ClientDto : Oauth2ClientDto {
    private var actions: String? = null

    constructor()

    @Builder(builderMethodName = "viewBuilder")
    constructor(
        id: String?,
        clientId: String?,
        clientName: String?,
        authorizationGrantTypes: String?,
        scopes: String?,
        createdDate: Date?,
        lastModifiedDate: Date?
    ) : super(id, clientId, clientName, authorizationGrantTypes, scopes, createdDate, lastModifiedDate) {
        actions = id
    }
}