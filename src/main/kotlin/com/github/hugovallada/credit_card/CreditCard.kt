package com.github.hugovallada.credit_card

import com.github.hugovallada.lock.Lock
import com.github.hugovallada.wallet.Wallet
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.PositiveOrZero

@Entity
@Table(name = "tb_credit_card")
class CreditCard(
    @field:NotBlank
    val number: String,
    @field:NotNull
    val issuedDate: LocalDateTime,
    @field:NotBlank
    val owner: String,
    @field:NotNull @field:PositiveOrZero
    val creditLimit: BigDecimal,
    @ManyToOne(cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    val expirationDate: ExpirationDate,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    var locked = false

    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE], fetch = FetchType.EAGER)
    @JoinTable(
        name = "tb_credit_card_associate_wallet",
        joinColumns = [JoinColumn(name = "wallet_id")],
        inverseJoinColumns = [JoinColumn(name = "credit_card_id")]
    )
    val wallets: MutableSet<Wallet> = mutableSetOf<Wallet>()


    fun associateWallet(wallet: Wallet){
        this.wallets.add(wallet)
    }
}