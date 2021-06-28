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
    @field:NotNull(message = "Shouldn't be null") @Enumerated(EnumType.STRING)
    val name: WalletType,
    @field:Email(message = "Invalid email") @field:NotBlank(message = "Shouldn't be blank")
    val email: String
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null


    @ManyToMany(mappedBy = "wallets")
    val creditCards: MutableSet<CreditCard> = mutableSetOf()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Wallet

        if (name != other.name) return false
        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + email.hashCode()
        return result
    }


}