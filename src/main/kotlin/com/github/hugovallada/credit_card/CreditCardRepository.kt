package com.github.hugovallada.credit_card

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface CreditCardRepository : JpaRepository<CreditCard, Long> {

    fun findByNumber(number: String) : CreditCard?
}