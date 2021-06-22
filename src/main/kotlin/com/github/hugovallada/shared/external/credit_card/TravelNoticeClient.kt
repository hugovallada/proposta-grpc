package com.github.hugovallada.shared.external.credit_card

import java.time.LocalDate

data class TravelNoticeClientRequest(
    val destino: String,
    val validoAte: LocalDate
)

data class TravelNoticeClientResponse(
    val resultado: String
)