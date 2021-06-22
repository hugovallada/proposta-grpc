package com.github.hugovallada.shared.extension

import com.github.hugovallada.TravelNoticeGrpcRequest
import com.github.hugovallada.credit_card.CreditCard
import com.github.hugovallada.travel.Travel
import java.time.LocalDate
import javax.validation.Constraint
import javax.validation.ConstraintViolationException
import javax.validation.Validator

fun TravelNoticeGrpcRequest.toModel(creditCard: CreditCard) = Travel(
    creditCard = creditCard,
    userAgent = this.userAgent,
    clientIp = this.clientIp,
    returnDate = LocalDate.parse(this.returnDate),
    destination = this.destination
)

fun Travel.isValid(validator: Validator): Travel {
    validator.validate(this).let {
        if(it.isNotEmpty()) throw ConstraintViolationException(it)
    }
    return this
}
