package com.webdroid.webdroidauthorizationserver.entity

import com.webdroid.webdroidauthorizationserver.entity.Oauth2AuthorizationConsent.AuthorizationConsentId
import jakarta.persistence.*
import lombok.*
import java.io.Serializable
import java.util.*

@Entity
@Table(name = "oauth2_authorization_consent")
@IdClass(
    AuthorizationConsentId::class
)
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class Oauth2AuthorizationConsent {
    @Id
    @Column(name = "registered_client_id")
    var registeredClientId: String? = null

    @Id
    @Column(name = "principal_name")
    var principalName: String? = null

    @Column(name = "authorities", length = 1000)
    var authorities: String? = null

    override fun hashCode(): Int {
        return Objects.hash(super.hashCode(), registeredClientId, principalName)
    }

    override fun toString(): String {
        return "AuthorizationConsent{" +
                "registeredClientId='" + registeredClientId + '\'' +
                ", principalName='" + principalName + '\'' +
                ", authorities='" + authorities + '\'' +
                '}'
    }

    class AuthorizationConsentId : Serializable {
        var registeredClientId: String? = null
        var principalName: String? = null
        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false
            val that = o as AuthorizationConsentId
            return registeredClientId == that.registeredClientId && principalName == that.principalName
        }

        override fun hashCode(): Int {
            return Objects.hash(registeredClientId, principalName)
        }
    }
}