/*
 * Copyright 2020-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webdroid.webdroidauthorizationserver.config.security

import com.maxmind.geoip2.DatabaseReader
import com.maxmind.geoip2.exception.GeoIp2Exception
import com.webdroid.webdroidauthorizationserver.config.CustomAuthenticationProvider
import com.webdroid.webdroidauthorizationserver.config.security.oauth2.CustomOAuth2UserService
import com.webdroid.webdroidauthorizationserver.config.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository
import com.webdroid.webdroidauthorizationserver.config.security.oauth2.OAuth2AuthenticationFailureHandler
import com.webdroid.webdroidauthorizationserver.config.security.oauth2.OAuth2AuthenticationSuccessHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.session.HttpSessionEventPublisher
import java.io.File
import java.io.IOException

/**
 * @author Joe Grandja
 * @since 0.1.0
 */
@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
class DefaultSecurityConfig @Autowired constructor(
    private val customAuthenticationProvider: CustomAuthenticationProvider,
    private val customOAuth2UserService: CustomOAuth2UserService,
    private val customLoginSuccessHandler: CustomLoginSuccessHandler,
    private val customLogoutSuccessHandler: CustomLogoutSuccessHandler,
    private val customLoginFailureHandler: CustomLoginFailureHandler,
    private val httpCookieOAuth2AuthorizationRequestRepository: HttpCookieOAuth2AuthorizationRequestRepository,
    private val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
    private val oAuth2AuthenticationFailureHandler: OAuth2AuthenticationFailureHandler
) {

    @Bean
    @Throws(Exception::class)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors()
            .and()
            .csrf { csrf -> csrf
                .disable()
            }
            .httpBasic { basic -> basic
                .disable()
            }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(
                        "/",
                        "/redirect",
                        "/favicon.ico",
                        "/*/*.png",
                        "/*/*.gif",
                        "/*/*.svg",
                        "/*/*.jpg",
                        "/*/*.html",
                        "/*/*.css",
                        "/*/*.js",
                        "/auth/**",
                        "/oauth2/**",
                        "/oauth2/authorization/**",
                        "/oauth2/authorization/github",
                        "/oauth2/authorization/github/**",
                    ).permitAll()
                    .requestMatchers("/invalidSession*")
                    .anonymous()
                    .requestMatchers("/user/updatePassword*")
                    .hasAuthority("CHANGE_PASSWORD_PRIVILEGE")
//                    .anyRequest()
//                    .hasAuthority("READ_PRIVILEGE")
                    .anyRequest().authenticated()
            }
            .formLogin { form -> form
                .defaultSuccessUrl("/")
                .failureUrl("/login?error=true")
                .successHandler(customLoginSuccessHandler)
                .failureHandler(customLoginFailureHandler)
                .loginPage("/login")
                .permitAll()
            }
            .logout { logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(customLogoutSuccessHandler)
            }
            .oauth2Login { oath ->
                oath
                    .loginPage("/login")
                    .authorizationEndpoint { auth ->
                        auth
                            .authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)
                    }
                    .userInfoEndpoint { userInfo ->
                        userInfo
                            .userService(customOAuth2UserService)
                    }
                    .successHandler(oAuth2AuthenticationSuccessHandler)
                    .failureHandler(oAuth2AuthenticationFailureHandler)
            }
        return http.build()
    }

    @Bean
    fun tokenAuthenticationFilter(): TokenAuthenticationFilter? {
        return TokenAuthenticationFilter()
    }

    @Autowired
    fun bindAuthenticationProvider(authenticationManagerBuilder: AuthenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider)
    }

    @Bean(name = ["GeoIPCountry"])
    @Throws(
        IOException::class,
        GeoIp2Exception::class
    )
    fun databaseReader(): DatabaseReader? {
        val resource = File("src/main/resources/maxmind/GeoLite2-Country.mmdb")
        return DatabaseReader.Builder(resource).build()
    }

//    @Bean
//    fun webSecurityExpressionHandler(): DefaultWebSecurityExpressionHandler? {
//        val expressionHandler = DefaultWebSecurityExpressionHandler()
//        expressionHandler.setRoleHierarchy(roleHierarchy)
//        return expressionHandler
//    }

    @Bean
    fun httpSessionEventPublisher(): HttpSessionEventPublisher? {
        return HttpSessionEventPublisher()
    }

}