package com.github.hugovallada.shared.external.analysis

data class AnalysisProposalResponse(
    val documento: String,
    val nome: String,
    val resultadoSolicitacao: String,
    val idProposta: String
)
