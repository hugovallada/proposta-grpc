package com.github.hugovallada.credit_card

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "tb_expiration_date")
class ExpirationDate(
    @Id
    val id: String,
    val day: Int,
    val issuedDate: LocalDateTime
) {

}
