package com.github.hugovallada.credit_card

import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.PositiveOrZero

@Entity
class CreditCard(
    @field:NotBlank
    val number: String,
    @field:NotNull
    val issuedDate: LocalDateTime,
    @field:NotBlank
    val owner: String,
    @field:NotNull @field:PositiveOrZero
    val creditLimit: BigDecimal,
    @ManyToOne(cascade = [CascadeType.MERGE,CascadeType.PERSIST])
    val expirationDate: ExpirationDate,
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}