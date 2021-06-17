package com.github.hugovallada.shared.extension

import com.github.hugovallada.NewProposalGrpcRequest
import com.github.hugovallada.address.Address
import com.github.hugovallada.proposal.Proposal
import javax.validation.ConstraintViolationException
import javax.validation.Validator

fun NewProposalGrpcRequest.toModel() = Proposal(
    document = this.document,
    email = this.email,
    name = this.name,
    address = Address(
        city = this.address.city,
        state = this.address.state,
        cep = this.address.cep,
        number = this.address.number,
        extension = this.address.extension
    ),
    salary = this.salary.toBigDecimal()
)

fun Proposal.isValid(validator: Validator): Proposal {
    validator.validate(this).let {
        if (it.isNotEmpty()) {
            throw ConstraintViolationException(it)
        }
    }
    return this
}