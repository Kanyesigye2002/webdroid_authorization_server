package com.webdroid.webdroidauthorizationserver.service

import com.akezimbira.akezimbirabackend.enums.GlobalOperator
import com.akezimbira.akezimbirabackend.enums.Operation
import com.webdroid.webdroidauthorizationserver.controller.SearchRequest
import com.webdroid.webdroidauthorizationserver.controller.SearchRequestDto
import jakarta.persistence.criteria.Predicate
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Polygon
import org.locationtech.jts.util.GeometricShapeFactory
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import java.util.*


@Service
class FiltersSpecificationService<T> {

    fun getSearchSpecification(searchRequestDto: SearchRequestDto): Specification<T> {
        return Specification<T> { root, _, criteriaBuilder ->
            criteriaBuilder.equal(
                root.get<T>(searchRequestDto.column),
                searchRequestDto.value
            )
        }
    }

    fun getSearchSpecification(searchRequest: SearchRequest): Specification<T> {
        return Specification<T> { root, _, criteriaBuilder ->
            val predicates = mutableListOf<Predicate>()
            for (requestDto in searchRequest.searchRequestDtos) {
                when (requestDto.operation) {
                    Operation.EQUAL -> {
                        val value = requestDto.value
                        if (value == "false" || value == "true") {
                            val equal = criteriaBuilder.equal(root.get<Boolean>(requestDto.column), value.toBoolean())
                            predicates.add(equal)
                        } else {
                            val equal = criteriaBuilder.equal(root.get<Any>(requestDto.column), requestDto.value)
                            predicates.add(equal)
                        }
                    }

                    Operation.LIKE -> {
                        val like = criteriaBuilder.like(root.get(requestDto.column), "%" + requestDto.value + "%")
                        predicates.add(like)
                    }

                    Operation.IN -> {
                        val split = requestDto.value.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val `in` = root.get<Any>(requestDto.column).`in`(Arrays.asList(*split))
                        predicates.add(`in`)
                    }

                    Operation.GREATER_THAN -> {
                        val greaterThan = criteriaBuilder.greaterThan(root.get(requestDto.column), requestDto.value)
                        predicates.add(greaterThan)
                    }

                    Operation.LESS_THAN -> {
                        val lessThan = criteriaBuilder.lessThan(root.get(requestDto.column), requestDto.value)
                        predicates.add(lessThan)
                    }

                    Operation.BETWEEN -> {
                        //"10, 20"
                        val split1 = requestDto.value.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val between =
                            criteriaBuilder.between(root.get(requestDto.column), split1[0].toLong(), split1[1].toLong())
                        predicates.add(between)
                    }

                    Operation.JOIN -> {
                        val join = criteriaBuilder.equal(
                            root.join<Any, Any>(requestDto.joinTable).get<Any>(requestDto.column),
                            requestDto.value
                        )
                        predicates.add(join)
                    }

                }
            }

            if (searchRequest.status != null)
                predicates.add(criteriaBuilder.equal(root.get<Any>("status"), searchRequest.status))

            if (searchRequest.bounds != null || searchRequest.center != null)
                predicates.add(
                    criteriaBuilder.equal(
                        criteriaBuilder.function(
                            "within", Boolean::class.java,
                            root.get<T>("point"), criteriaBuilder.literal(createRectangleShape(searchRequest))
                        ), true
                    )
                )

            criteriaBuilder.and(*predicates.toTypedArray())
            if (searchRequest.globalOperator == GlobalOperator.AND) {
                criteriaBuilder.and(*predicates.toTypedArray())
            } else {
                criteriaBuilder.or(*predicates.toTypedArray())
            }
        }
    }

    fun createRectangleShape(searchRequest: SearchRequest): Polygon? {
        if (searchRequest.bounds == null || searchRequest.center == null)
            return null
        val geometricShapeFactory = GeometricShapeFactory()
        geometricShapeFactory.setHeight(
            Coordinate(searchRequest.bounds.ne.lng, searchRequest.bounds.ne.lat).distance(
                Coordinate(searchRequest.bounds.ne.lng, searchRequest.bounds.sw.lat)
            )
        )
        geometricShapeFactory.setWidth(
            Coordinate(searchRequest.bounds.ne.lng, searchRequest.bounds.ne.lat).distance(
                Coordinate(searchRequest.bounds.sw.lng, searchRequest.bounds.ne.lat)
            )
        )

        geometricShapeFactory.setNumPoints(64) // adjustable
        geometricShapeFactory.setCentre(Coordinate(searchRequest.center.lng, searchRequest.center.lat))

        return geometricShapeFactory.createRectangle()
    }

}