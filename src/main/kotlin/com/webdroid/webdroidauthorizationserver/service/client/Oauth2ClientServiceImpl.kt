package com.webdroid.webdroidauthorizationserver.service.client

import com.naharoo.commons.mapstruct.MappingFacade
import com.webdroid.webdroidauthorizationserver.dto.client.ViewOauth2ClientDto
import com.webdroid.webdroidauthorizationserver.repository.Oauth2ClientRepository
import org.springframework.stereotype.Service

@Service
class Oauth2ClientServiceImpl(private val oauth2ClientRepo: Oauth2ClientRepository, private val mapper: MappingFacade) : Oauth2ClientService {

    override fun find(): List<ViewOauth2ClientDto?>? {
        val data = oauth2ClientRepo.findAll()
        return mapper.mapAsList(data, ViewOauth2ClientDto::class.java)
    }

    override fun delete(ids: List<String?>?) {
        oauth2ClientRepo.deleteAllById(ids!!)
    }

}