package com.github.hugovallada.wallet

import com.github.hugovallada.credit_card.CreditCard
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_wallet")
class Wallet(
    @field:NotBlank
    val name: String,
    @field:Email @field:NotBlank
    val email: String
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null


    @field:NotNull @Valid
    @ManyToMany(mappedBy = "wallets", fetch = FetchType.EAGER)
    val creditCards: MutableSet<CreditCard> = mutableSetOf()
}