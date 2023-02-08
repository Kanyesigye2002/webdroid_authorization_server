package com.webdroid.webdroidauthorizationserver.dto

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import lombok.experimental.SuperBuilder
import java.io.Serializable
import java.util.*

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
abstract class BaseDto : Serializable {
    protected var createdBy: String? = null
    protected var createdDate: Date? = null
    protected var lastModifiedBy: String? = null
    protected var lastModifiedDate: Date? = null
}