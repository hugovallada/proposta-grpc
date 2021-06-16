package com.github.hugovallada.shared.external.analysis

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client("http://localhost:9999")
interface AnalysisClient {

    @Post("/api/solicitacao")
    fun analyze(@Body analysisRequest: AnalysisProposalRequest) : HttpResponse<AnalysisProposalResponse?>

}