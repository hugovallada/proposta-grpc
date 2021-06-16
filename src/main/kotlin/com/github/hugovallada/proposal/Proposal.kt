package com.github.hugovallada.proposal

import com.github.hugovallada.address.Address
import java.math.BigDecimal
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "tb_proposal")
class Proposal(
    val document: String,
    val email: String,
    val name: String,
    @ManyToOne(cascade = [CascadeType.MERGE])
    val address: Address,
    val salary: BigDecimal
) {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null

    val externalId: UUID = UUID.randomUUID()
}