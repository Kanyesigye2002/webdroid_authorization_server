package com.webdroid.webdroidauthorizationserver.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import lombok.*
import java.time.Instant

@Entity
@Table(name = "oauth2_registered_client")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class Oauth2Client {
    @Id
    private val id: String? = null

    @Column(name = "client_id")
    private val clientId: String? = null

    @Column(name = "client_id_issued_at")
    private val clientIdIssuedAt: Instant? = null

    @Column(name = "client_secret")
    private val clientSecret: String? = null

    @Column(name = "client_secret_expires_at")
    private val clientSecretExpiresAt: Instant? = null

    @Column(name = "client_name")
    private val clientName: String? = null

    @Column(name = "client_authentication_methods", length = 1000)
    private val clientAuthenticationMethods: String? = null

    @Column(name = "authorization_grant_types", length = 1000)
    private val authorizationGrantTypes: String? = null

    @Column(name = "redirect_uris", length = 1000)
    private val redirectUris: String? = null

    @Column(name = "scopes", length = 1000)
    private val scopes: String? = null

    @Column(name = "client_settings", length = 2000)
    private val clientSettings: String? = null

    @Column(name = "token_settings", length = 2000)
    private val tokenSettings: String? = null

}