package com.github.hugovallada.shared.external.credit_card

import io.micronaut.core.annotation.Introspected

@Introspected
data class LockCreditCardClientRequest(
    val sistemaResponsavel: String
)

@Introspected

data class LockCreditCardClientResponse(
    val resultado: String,
){
    init {
        println("Response...")
    }
}
