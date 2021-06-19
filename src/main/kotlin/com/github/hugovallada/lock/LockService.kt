package com.github.hugovallada.lock

import com.github.hugovallada.credit_card.CreditCardRepository
import com.github.hugovallada.shared.exception.TargetNotfoundException
import com.github.hugovallada.shared.exception.UnprocessableEntityException
import com.github.hugovallada.shared.external.credit_card.CreditCardClient
import com.github.hugovallada.shared.external.credit_card.LockCreditCardClientRequest
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.exceptions.HttpException
import io.reactivex.exceptions.OnErrorNotImplementedException
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LockService(
    @Inject private val creditCardRepository: CreditCardRepository,
    @Inject private val creditCardClient: CreditCardClient,
    @Inject private val lockRepository: LockRepository
) {

    private val LOG = LoggerFactory.getLogger(this::class.java)

    fun lock(lock: Lock): Boolean {
        if(lock.creditCard.locked) throw UnprocessableEntityException("The credit card is already locked")

        try{
            creditCardClient.lockCreditCard(lock.creditCard.number,sistemaResponsavel = "grpc").run {
                lockRepository.save(lock)
                lock.creditCard.locked = true
                creditCardRepository.update(lock.creditCard)
            }
        }catch (exception: HttpClientResponseException){
            LOG.info("Something happened, i wasn't possible to lock the credit card")
        }

        return lock.creditCard.locked

    }
}