package com.github.hugovallada.lock

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface LockRepository : JpaRepository<Lock, Long> {
}