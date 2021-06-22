package com.github.hugovallada.travel

import com.github.hugovallada.TravelNoticeGrpcRequest
import com.github.hugovallada.credit_card.CreditCardRepository
import com.github.hugovallada.shared.exception.TargetNotfoundException
import com.github.hugovallada.shared.extension.isValid
import com.github.hugovallada.shared.extension.toModel
import com.github.hugovallada.shared.external.credit_card.CreditCardClient
import com.github.hugovallada.shared.external.credit_card.TravelNoticeClientRequest
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@Singleton
class TravelNoticeService(
    @Inject private val creditCardRepository: CreditCardRepository,
    @Inject private val validator: Validator,
    @Inject private val travelRepository: TravelRepository,
    @Inject private val creditCardClient: CreditCardClient
) {

    fun notificate(request: TravelNoticeGrpcRequest): String {

        val creditCard = creditCardRepository.findByNumber(request.cardNumber) ?: throw TargetNotfoundException("Couldn't find credit card with number ${request.cardNumber}")

        request.toModel(creditCard).isValid(validator).run {
            return try{
                creditCardClient.notificate(creditCard.number, TravelNoticeClientRequest(destino = destination, validoAte = returnDate))
                    .doOnSuccess {
                        travelRepository.save(this)
                    }.subscribe()
                "Add travel for credit card number ${creditCard.number}"
            }catch (exception: HttpClientResponseException){
                "Travel for credit card number ${creditCard.number} couldn't be saved"
            }
        }



    }


}