package com.github.hugovallada.biometry

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface BiometryRepository : JpaRepository<Biometry, Long> {
    fun existsByFingerPrint(fingerPrint: String) : Boolean
}