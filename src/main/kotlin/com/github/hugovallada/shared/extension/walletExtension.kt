package com.github.hugovallada.shared.extension

import com.github.hugovallada.AssociateWalletGrpcRequest
import com.github.hugovallada.credit_card.CreditCardRepository
import com.github.hugovallada.wallet.Wallet
import com.github.hugovallada.wallet.WalletType
import javax.validation.ConstraintViolationException
import javax.validation.Validator

fun AssociateWalletGrpcRequest.toModel()=
    Wallet(name = WalletType.of(this.wallet), email = this.email)

fun Wallet.isValid(validator: Validator): Wallet {
    val errors = validator.validate(this)
    if(errors.isNotEmpty()) throw ConstraintViolationException(errors)

    return this
}