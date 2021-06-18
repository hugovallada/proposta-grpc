package com.github.hugovallada.biometry

import com.github.hugovallada.credit_card.CreditCard
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Biometry(
    val fingerPrint: String,
    @CreationTimestamp
    val storageDate: LocalDateTime,
    @OneToMany
    val creditCard: CreditCard
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}