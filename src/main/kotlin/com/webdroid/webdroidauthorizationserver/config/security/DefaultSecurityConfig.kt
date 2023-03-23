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

import com.webdroid.webdroidauthorizationserver.config.AppUserDetailsService
import com.webdroid.webdroidauthorizationserver.config.CustomAuthenticationProvider
import com.webdroid.webdroidauthorizationserver.config.security.oauth2.CustomOAuth2UserService
import com.webdroid.webdroidauthorizationserver.config.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository
import com.webdroid.webdroidauthorizationserver.config.security.oauth2.OAuth2AuthenticationFailureHandler
import com.webdroid.webdroidauthorizationserver.config.security.oauth2.OAuth2AuthenticationSuccessHandler
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.session.HttpSessionEventPublisher


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
    private val oAuth2AuthenticationFailureHandler: OAuth2AuthenticationFailureHandler,
    private val tokenAuthenticationFilter: TokenAuthenticationFilter
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun authenticationProvider(userService: AppUserDetailsService, passwordEncoder: PasswordEncoder): AuthenticationProvider {
        val provider = DaoAuthenticationProvider()
        provider.setUserDetailsService(userService)
        provider.setPasswordEncoder(passwordEncoder)
        return provider
    }

//    @Bean
//    fun tokenAuthenticationFilter(): TokenAuthenticationFilter {
//        return TokenAuthenticationFilter()
//    }

    @Bean
    @Throws(Exception::class)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors()
            .and()
            .csrf { csrf -> csrf
                .disable()
            }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers(
                        "/favicon.ico",
                        "/*/*.png",
                        "/*/*.gif",
                        "/*/*.svg",
                        "/*/*.jpg",
                        "/*/*.html",
                        "/*/*.css",
                        "/*/*.js",
                        "/api/v1/signup",
                        "/api/v1/login",
                        "/auth/**",
                        "/oauth2/**",
                        "/oauth2/authorization/**",
                        "/oauth2/authorization/github",
                        "/oauth2/authorization/github/**",
                    ).permitAll()
                    .requestMatchers("/invalidSession*")
                    .anonymous()
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
            .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }

    @Autowired
    fun bindAuthenticationProvider(authenticationManagerBuilder: AuthenticationManagerBuilder) {
        authenticationManagerBuilder.authenticationProvider(customAuthenticationProvider)
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