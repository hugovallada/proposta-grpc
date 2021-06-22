package com.github.hugovallada.shared.external.credit_card

data class AssociateWalletClientRequest(val email: String, val carteira: String)

data class AssociateWalletClientResponse(val resultado: String, val id: String)