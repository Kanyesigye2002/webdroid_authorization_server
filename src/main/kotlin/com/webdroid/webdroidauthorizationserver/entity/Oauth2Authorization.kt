package com.webdroid.webdroidauthorizationserver.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import lombok.*
import java.time.Instant

@Entity
@Table(name = "oauth2_authorization")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class Oauth2Authorization {
    @Id
    @Column
    private val id: String? = null

    @Column(name = "registered_client_Id")
    private val registeredClientId: String? = null

    @Column(name = "principal_name")
    private val principalName: String? = null

    @Column(name = "authorization_grant_type")
    private val authorizationGrantType: String? = null

    @Column(name = "attributes", columnDefinition = "TEXT")
    private val attributes: String? = null

    @Column(name = "state", length = 500)
    private val state: String? = null

    @Column(name = "authorization_code_value", columnDefinition = "TEXT")
    private val authorizationCodeValue: String? = null

    @Column(name = "authorization_code_issued_at")
    private val authorizationCodeIssuedAt: Instant? = null

    @Column(name = "authorization_code_expires_at")
    private val authorizationCodeExpiresAt: Instant? = null

    @Column(name = "authorization_code_metadata")
    private val authorizationCodeMetadata: String? = null

    @Column(name = "access_token_value", columnDefinition = "TEXT")
    private val accessTokenValue: String? = null

    @Column(name = "access_token_issued_at")
    private val accessTokenIssuedAt: Instant? = null

    @Column(name = "access_token_expires_at")
    private val accessTokenExpiresAt: Instant? = null

    @Column(name = "access_token_metadata", columnDefinition = "TEXT")
    private val accessTokenMetadata: String? = null

    @Column(name = "access_token_type")
    private val accessTokenType: String? = null

    @Column(name = "access_token_scopes", columnDefinition = "TEXT")
    private val accessTokenScopes: String? = null

    @Column(name = "refresh_token_value", length = 4000)
    private val refreshTokenValue: String? = null

    @Column(name = "refresh_token_issued_at")
    private val refreshTokenIssuedAt: Instant? = null

    @Column(name = "refresh_token_expires_at")
    private val refreshTokenExpiresAt: Instant? = null

    @Column(name = "refresh_token_metadata", columnDefinition = "TEXT")
    private val refreshTokenMetadata: String? = null

    @Column(name = "oidc_id_token_value", columnDefinition = "TEXT")
    private val oidcIdTokenValue: String? = null

    @Column(name = "oidc_id_token_issued_at")
    private val oidcIdTokenIssuedAt: Instant? = null

    @Column(name = "oidc_id_token_expires_at")
    private val oidcIdTokenExpiresAt: Instant? = null

    @Column(name = "oidc_id_token_metadata", columnDefinition = "TEXT")
    private val oidcIdTokenMetadata: String? = null

    @Column(name = "oidc_id_token_claims", columnDefinition = "TEXT")
    private val oidcIdTokenClaims: String? = null

}