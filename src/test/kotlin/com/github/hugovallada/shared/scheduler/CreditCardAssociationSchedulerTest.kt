package com.github.hugovallada.shared.scheduler

import com.github.hugovallada.address.Address
import com.github.hugovallada.proposal.Proposal
import com.github.hugovallada.proposal.ProposalRepository
import com.github.hugovallada.proposal.ProposalStatus
import com.github.hugovallada.shared.external.credit_card.CreditCardClient
import com.github.hugovallada.shared.external.credit_card.CreditCardClientResponse
import com.github.hugovallada.shared.external.credit_card.ExpirationDateClientResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.reactivex.Single
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.inject.Inject

@MicronautTest
internal class CreditCardAssociationSchedulerTest(
    private val proposalRepository: ProposalRepository
){
    @Inject
    lateinit var creditCardClient: CreditCardClient



    @Test
    fun `should associate a credit card when a proposal is eligible and does not have a credit card`(){
        val proposal = Proposal(
            document = "11793195080",
            email = "email@email.com",
            name = "Hugo",
            address = Address(city = "RP", state = "SP", cep = "19298282", number = "223", extension = ""),
            salary = BigDecimal(2500)
        )

        proposal.status = ProposalStatus.ELIGIBLE
        proposalRepository.save(proposal)



        Mockito.`when`(creditCardClient.getCreditCard(proposal.id.toString())).thenReturn(
            Single.just(CreditCardClientResponse(
                id = "6929-6084-4630-3370",
                emitidoEm = LocalDateTime.now(),
                titular = proposal.name,
                bloqueios = listOf<String>(),
                avisos =  listOf<String>(),
                carteiras = listOf<String>(),
                parcelas = listOf<String>(),
                limite = 2000,
                renegociacao = null,
                vencimento = ExpirationDateClientResponse(id = "28288282",dia = 20,dataDeCriacao = LocalDateTime.now()),
                idProposta = proposal.id.toString()
            ))
        )

        CreditCardAssociationScheduler(proposalRepository, creditCardClient).associate()

        assertTrue(proposalRepository.findByDocument("11793195080")!!.creditCard != null)
    }

    @Test
    fun `should do nothing when the proposal is not eligible`(){
        val proposal = Proposal(
            document = "32605826066",
            email = "email@email.com",
            name = "Hugo",
            address = Address(city = "RP", state = "SP", cep = "19298282", number = "223", extension = ""),
            salary = BigDecimal(2500)
        )

        proposal.status = ProposalStatus.NOT_ELIGIBLE
        proposalRepository.save(proposal)

        CreditCardAssociationScheduler(proposalRepository, creditCardClient).associate()

        assertTrue(proposalRepository.findByDocument("32605826066")!!.creditCard == null)
    }

    @Test
    fun `should not associate a credit card when a client exception happens`(){
        val proposal = Proposal(
            document = "85805077078",
            email = "email@email.com",
            name = "Hugo",
            address = Address(city = "RP", state = "SP", cep = "19298282", number = "223", extension = ""),
            salary = BigDecimal(2500)
        )

        proposal.status = ProposalStatus.ELIGIBLE
        proposalRepository.save(proposal)



        Mockito.`when`(creditCardClient.getCreditCard(proposal.id.toString())).
            thenThrow(HttpClientResponseException::class.java)


        CreditCardAssociationScheduler(proposalRepository, creditCardClient).associate()

        assertTrue(proposalRepository.findByDocument("85805077078")!!.creditCard == null)
    }

    @MockBean(CreditCardClient::class)
    fun mockCrediCardClient(): CreditCardClient {
        return Mockito.mock(CreditCardClient::class.java)
    }

}