package com.github.hugovallada.travel

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface TravelRepository : JpaRepository<Travel, Long> {
}