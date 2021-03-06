package com.github.hugovallada.proposal

import com.github.hugovallada.address.Address
import com.github.hugovallada.credit_card.CreditCard
import com.github.hugovallada.shared.validator.Document
import java.math.BigDecimal
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive

@Entity
@Table(name = "tb_proposal")
class Proposal(
    @field:NotBlank @field:Document
    val document: String,
    @field:NotBlank @field:Email
    val email: String,
    @field:NotBlank
    val name: String,
    @ManyToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @field:Valid
    val address: Address,
    @field:NotNull @field:Positive
    val salary: BigDecimal
) {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null

    @Enumerated(EnumType.STRING)
    var status: ProposalStatus? = null

    @OneToOne(cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    @JoinColumn(name = "credit_card_id", referencedColumnName = "id")
    var creditCard: CreditCard? = null

    val externalId: UUID = UUID.randomUUID()
}