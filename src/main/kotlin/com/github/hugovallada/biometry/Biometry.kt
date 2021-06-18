package com.github.hugovallada.biometry

import com.github.hugovallada.credit_card.CreditCard
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_biometry")
class Biometry(
    @field:NotBlank
    val fingerPrint: String,
    @ManyToOne @field:NotNull @field:Valid
    val creditCard: CreditCard
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    val externalId: UUID = UUID.randomUUID()

    @CreationTimestamp
    val storageDate: LocalDateTime? = null

}