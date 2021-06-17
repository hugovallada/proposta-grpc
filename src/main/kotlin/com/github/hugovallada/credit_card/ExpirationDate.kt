package com.github.hugovallada.credit_card

import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class ExpirationDate(
    @Id
    val id: String,
    val day: Int,
    val issuedDate: LocalDateTime
) {

}
