package com.github.hugovallada.shared.external.credit_card

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import io.reactivex.Flowable
import io.reactivex.Single

@Client("http://localhost:8888")
interface CreditCardClient {

    @Get("/api/cartoes?idProposta={idProposta}")
    fun getCreditCard(@QueryValue idProposta: String) : Single<CreditCardClientResponse>

    @Post("/api/cartoes/{id}/bloqueios")
    fun lockCreditCard(@PathVariable id: String, sistemaResponsavel: String) : HttpResponse<LockCreditCardClientResponse>

}