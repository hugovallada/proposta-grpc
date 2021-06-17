package com.github.hugovallada.shared.external.analysis

import com.github.hugovallada.proposal.Proposal

data class AnalysisProposalRequest(
    val documento: String,
    val nome: String,
    val idProposta: String
) {
    constructor(proposal: Proposal) : this(
        documento = proposal.document,
        nome = proposal.name,
        idProposta = proposal.id.toString()
    )
}
