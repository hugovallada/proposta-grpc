package com.github.hugovallada.proposal.detail

import com.github.hugovallada.ProposalStatusGrpc
import com.github.hugovallada.ProposalStatusGrpcRequest
import com.github.hugovallada.address.Address
import com.github.hugovallada.credit_card.CreditCard
import com.github.hugovallada.credit_card.ExpirationDate
import com.github.hugovallada.proposal.Proposal
import com.github.hugovallada.proposal.ProposalRepository
import com.github.hugovallada.proposal.ProposalStatus
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.inject.Singleton
import javax.persistence.EntityManager

@MicronautTest(transactional = false)
internal class ProposalDetailEndpointTest(
    private val grpcClient: ProposalStatusGrpc.ProposalStatusBlockingStub,
    private val proposalRepository: ProposalRepository,
    private val entityManager: EntityManager
){

    @Test
    internal fun `should return not exists when a invalid proposal is send`() {
        val id = UUID.randomUUID().toString()
        assertThrows<StatusRuntimeException>{
            grpcClient.watch(ProposalStatusGrpcRequest.newBuilder()
                .setId(id).build())
        }.run {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Can't find proposal with id: $id", status.description)
        }
    }

    @Test
    internal fun `shoud return the proposal when a valid proposal id is send`() {
        val proposal = Proposal(
            document = "11793195080",
            email = "email@email.com",
            name = "Hugo",
            address = Address("SP", "SP", "90228282", "999", ""),
            salary = BigDecimal(2500)
        )

        val creditCard = CreditCard(
            "6929-6084-4630-3370", LocalDateTime.now(), "Hugo",
            BigDecimal(2000), ExpirationDate
                ("1", 20, LocalDateTime.now())
        )

        proposal.status = ProposalStatus.ELIGIBLE
        proposal.creditCard = creditCard

        proposalRepository.save(proposal)

        val response =
            grpcClient.watch(ProposalStatusGrpcRequest.newBuilder().setId(proposal.externalId.toString()).build())


        assertEquals(creditCard.number,response.creditCard)
        assertEquals(proposal.document, response.documet)
        assertEquals(proposal.name, response.name)
        assertEquals(proposal.status.toString(), response.status.toString())
        assertEquals(proposal.email, response.email)

    }

    @Factory
    internal class grpcFactory{

        @Singleton
        fun generateGrpcClient(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): ProposalStatusGrpc.ProposalStatusBlockingStub? {
            return ProposalStatusGrpc.newBlockingStub(channel)
        }
    }
}