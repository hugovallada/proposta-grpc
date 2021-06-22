package com.github.hugovallada.travel

import com.github.hugovallada.credit_card.CreditCard
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.Future
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "tb_travel")
class Travel(
    @ManyToOne
    @JoinColumn(name = "credit_card_id", referencedColumnName = "id")
    @field:NotNull @Valid
    val creditCard: CreditCard,
    @field:NotBlank
    val userAgent: String,
    @field:NotBlank
    val clientIp: String,
    @field:NotNull @field:Future
    val returnDate: LocalDate,
    @field:NotBlank
    val destination: String
) {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @CreationTimestamp
    var noticeDate: LocalDateTime? = null

}