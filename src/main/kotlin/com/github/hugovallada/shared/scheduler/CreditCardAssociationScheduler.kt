package com.github.hugovallada.shared.scheduler

import com.github.hugovallada.proposal.ProposalRepository
import com.github.hugovallada.shared.external.credit_card.CreditCardClient
import com.github.hugovallada.shared.external.credit_card.CreditCardClientResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.scheduling.annotation.Scheduled
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class CreditCardAssociationScheduler(
    private val proposalRepository: ProposalRepository,
    private val creditCardClient: CreditCardClient
) {

    private val LOG : Logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(fixedRate = "100s")
    fun associate(){
        proposalRepository.proposalReadyForAssociation().run {
            forEach {
                proposal ->
                try{
                    creditCardClient.getCreditCard(proposal.id.toString()).doOnSuccess {
                        creditCard ->
                        proposal.creditCard = creditCard.toModel()
                        proposalRepository.update(proposal)
                    }.subscribe()
                }catch (exception: HttpClientResponseException){
                    LOG.info("Something went wrong")
                }
            }
        }
    }

}