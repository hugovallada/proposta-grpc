package com.github.hugovallada.lock

import com.github.hugovallada.credit_card.CreditCard
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "tb_lock")
class Lock(
    @field:NotBlank(message = "clientIp shouldn't be blank")
    val clientIp : String,
    @field:NotBlank(message = "userAgent shouldn't be blank")
    val userAgent: String,
    @ManyToOne
    val creditCard: CreditCard
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @CreationTimestamp
    var lockTimestamp: LocalDateTime? = null
}