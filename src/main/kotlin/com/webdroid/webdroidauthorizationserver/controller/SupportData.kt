package com.webdroid.webdroidauthorizationserver.controller

import com.akezimbira.akezimbirabackend.enums.GlobalOperator
import com.akezimbira.akezimbirabackend.enums.Operation
import com.webdroid.webdroidauthorizationserver.entity.Role
import com.webdroid.webdroidauthorizationserver.entity.User
import lombok.Getter
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class SupportData

data class LoginResponse(
    val token: String?,
    val error: String?,
    val user: UserDetails?
)

data class UserDetails(
    val id: String,
    val avatarUrl: String?,
    val username: String,
    val firstName: String?,
    val lastName: String?,
    var name: String?,
    var title: String?,
    val email: String?,
    var phoneNumber: String?,
    var country: String?,
    var address: String?,
    val state: String?,
    val city: String?,
    val about: String?,
    val role: Role?
) {
    constructor(user: User) : this(
        user.id,
        user.avatarUrl,
        user.username,
        user.firstName,
        user.lastName,
        user.name,
        user.title,
        user.email,
        user.phoneNumber,
        user.country,
        user.address,
        user.state,
        user.city,
        user.about,
        user.role
    ) {
        if (user.name == null) {
            name = "${user.firstName} ${user.lastName}"
        }
    }
}

data class LoginRequest(val username: String, val password: String)

class Point(
    var lng: Double,
    var lat: Double,
)

class Bounds(
    val sw: Point,
    val ne: Point,
)

@Getter
class PageRequestDto(
    var pageNo: Int = 0,
    var pageSize: Int = 10,
    var sortBy: String = "id",
    var sortDirection: Sort.Direction = Sort.Direction.ASC,
) {
    fun getPageable(): Pageable {
        return PageRequest.of(
            pageNo,
            pageSize,
            Sort.by(sortDirection, sortBy)
        )
    }
}

class SearchRequestDto (
    var column: String = "",
    var value: String = "",
    var joinTable: String? = null,
    var operation: Operation = Operation.EQUAL,
)



class SearchRequest(
    val center: Point?,
    val bounds: Bounds?,
    val status: String?,
    val page: PageRequestDto,
    val searchRequestDtos: MutableList<SearchRequestDto>,
    val globalOperator: GlobalOperator,
)
