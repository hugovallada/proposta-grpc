package com.github.hugovallada.shared.extension

import com.github.hugovallada.BiometryGrpcRequest
import com.github.hugovallada.biometry.Biometry
import com.github.hugovallada.credit_card.CreditCardRepository
import com.github.hugovallada.shared.exception.TargetNotfoundException
import javax.validation.ConstraintViolationException
import javax.validation.Validator

fun BiometryGrpcRequest.toModel(creditCardRepository: CreditCardRepository) = Biometry(
    fingerPrint = this.fingerPrint,
    creditCard = creditCardRepository.findByNumber(this.cardNumber) ?: throw TargetNotfoundException("Credit card with number $cardNumber not found")
)

fun Biometry.isValid(validator: Validator): Biometry {
    validator.validate(this).run {
        if(isNotEmpty()) throw ConstraintViolationException(this)
    }
    return this
}