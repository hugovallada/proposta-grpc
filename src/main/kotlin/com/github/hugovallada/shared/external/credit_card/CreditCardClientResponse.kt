package com.github.hugovallada.shared.external.credit_card

import com.github.hugovallada.credit_card.CreditCard
import com.github.hugovallada.credit_card.ExpirationDate
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreditCardClientResponse(
    val id: String,
    val emitidoEm: LocalDateTime,
    val titular: String,
    val bloqueios: List<String>,
    val avisos: List<String>,
    val carteiras: List<String>,
    val parcelas: List<String>,
    val limite: Int,
    val renegociacao: RenegotiationClientResponse?,
    val vencimento: ExpirationDateClientResponse,
    val idProposta: String
){
    fun toModel() = CreditCard(
        number = this.id,
        issuedDate = this.emitidoEm,
        owner = this.titular,
        creditLimit = this.limite.toBigDecimal(),
        expirationDate = this.vencimento.toModel()
    )
}

data class RenegotiationClientResponse(
    val id: String,
    val quantidade: Int,
    val valor: BigDecimal,
    val dataDeCriacao: LocalDateTime
)

data class ExpirationDateClientResponse(
    val id: String,
    val dia: Int,
    val dataDeCriacao: LocalDateTime
){
    fun toModel() = ExpirationDate(
        id = this.id,
        day = this.dia,
        issuedDate = this.dataDeCriacao
    )
}
