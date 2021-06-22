package com.github.hugovallada.travel

import com.github.hugovallada.credit_card.CreditCard
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "tb_travel")
class Travel(
    @ManyToOne
    @JoinColumn(name = "credit_card_id", referencedColumnName = "id")
    val creditCard: CreditCard,
    val userAgent: String,
    val clientIp: String,
    val returnDate: LocalDate,
    val destination: String
) {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    @CreationTimestamp
    var noticeDate: LocalDateTime? = null

}