package com.github.hugovallada.shared.extension

import com.github.hugovallada.LockGrpcRequest
import com.github.hugovallada.credit_card.CreditCardRepository
import com.github.hugovallada.lock.Lock
import com.github.hugovallada.shared.exception.TargetNotfoundException
import javax.validation.ConstraintViolationException
import javax.validation.Validator


fun LockGrpcRequest.toModel(creditCardRepository: CreditCardRepository) = Lock(
    clientIp = this.clientIp,
    userAgent = this.userAgent,
    creditCard = creditCardRepository.findByNumber(this.cardNumber) ?: throw TargetNotfoundException("There's no credit card with number ${this.cardNumber}")
)

fun Lock.isValid(validator: Validator): Lock {
    validator.validate(this).let {
        if(it.isNotEmpty()) throw ConstraintViolationException(it)
    }
    return this
}