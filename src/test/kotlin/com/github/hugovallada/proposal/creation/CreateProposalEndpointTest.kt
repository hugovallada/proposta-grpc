package com.github.hugovallada.proposal.creation

import com.github.hugovallada.AddressGrpc
import com.github.hugovallada.CreateProposalGrpc
import com.github.hugovallada.NewProposalGrpcRequest
import com.github.hugovallada.address.Address
import com.github.hugovallada.proposal.Proposal
import com.github.hugovallada.proposal.ProposalRepository
import com.github.hugovallada.proposal.ProposalStatus
import com.github.hugovallada.shared.external.analysis.AnalysisClient
import com.github.hugovallada.shared.external.analysis.AnalysisProposalRequest
import com.github.hugovallada.shared.external.analysis.AnalysisProposalResponse
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.*
import javax.inject.Singleton
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.Mockito
import java.math.BigDecimal
import javax.inject.Inject
import javax.transaction.Transactional

@MicronautTest(transactional = false)
internal class CreateProposalEndpointTest(
    private val grpcClient: CreateProposalGrpc.CreateProposalBlockingStub,
    private val proposalRepository: ProposalRepository
) {

    @Inject
    lateinit var analysisClient: AnalysisClient

    @BeforeEach
    internal fun setUp() {
        proposalRepository.deleteAll()
    }


    @Test
    internal fun `should create a new proposal in the database and return it's external id when it's eligible`(){
        val request = NewProposalGrpcRequest.newBuilder()
            .setDocument("86852124053")
            .setEmail("email@email.com")
            .setName("Hugo")
            .setSalary("2500")
            .setAddress(AddressGrpc.newBuilder().setCep("14090090").setCity("São Paulo").setState("São Paulo").setNumber("999")
                .setExtension("").build())
            .build()

        Mockito.`when`(analysisClient.analyze(AnalysisProposalRequest("86852124053","Hugo","1")))
            .thenReturn(HttpResponse.ok(AnalysisProposalResponse("86852124053","Hugo","SEM_RESTRICAO","1")))

        val response = grpcClient.create(request)
        with(response){
            assertTrue(idProposal.length == 36)
            assertTrue(proposalRepository.existsByDocument("86852124053"))
        }
    }

    @Test
    internal fun `should create a new proposal in the database and return it's external id when it's not eligible`(){
        val request = NewProposalGrpcRequest.newBuilder()
            .setDocument("32605826066")
            .setEmail("email@email.com")
            .setName("Hugo")
            .setSalary("2500")
            .setAddress(AddressGrpc.newBuilder().setCep("14090090").setCity("São Paulo").setState("São Paulo").setNumber("999")
                .setExtension("").build())
            .build()

        Mockito.`when`(analysisClient.analyze(AnalysisProposalRequest("32605826066","Hugo","2")))
            .thenThrow(HttpClientResponseException::class.java)

        val response = grpcClient.create(request)
        with(response){
            assertTrue(idProposal.length == 36)
            assertTrue(proposalRepository.existsByDocument("32605826066"))
            assertEquals(ProposalStatus.NOT_ELIGIBLE, proposalRepository.findByDocument("32605826066")?.status)
        }
    }



    @Test
    internal fun `should return status already exists when someone tries to create a proposal with an already existing document`(){
        val proposal = Proposal("32605826066","email@email.com","Hugo", Address("Itagua","São Paulo", "14999999","888",""),
            BigDecimal(2500)
        )
        proposalRepository.save(proposal)

        val request = NewProposalGrpcRequest.newBuilder()
            .setDocument("32605826066")
            .setEmail("email@email.com")
            .setName("Hugo")
            .setSalary("2500")
            .setAddress(AddressGrpc.newBuilder().setCep("14090090").setCity("São Paulo").setState("São Paulo").setNumber("999")
                .setExtension("").build())
            .build()

        assertThrows<StatusRuntimeException> {
            grpcClient.create(request)
        }.run {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("There's already 1 proposal with this document",status.description)
        }
    }

    @Test
    internal fun `should return status unknow when a unknow event happens`(){
        val request = NewProposalGrpcRequest.newBuilder()
            .setDocument("9090")
            .setEmail("email@email.com")
            .setName("Hugo")
            .setSalary("abcx")
            .setAddress(AddressGrpc.newBuilder().setCep("14090090").setCity("São Paulo").setState("São Paulo").setNumber("999")
                .setExtension("").build())
            .build()

        assertThrows<StatusRuntimeException> {
            grpcClient.create(request)
        }.run{
            assertEquals(Status.UNKNOWN.code, status.code)
            assertEquals("Unknow error ...", status.description)
        }

    }

    @Test
    internal fun `should return status invalid argument if validation fails`() {
        val request = NewProposalGrpcRequest.newBuilder()
            .setDocument("9090")
            .setEmail("email@email.com")
            .setName("Hugo")
            .setSalary("2500")
            .setAddress(AddressGrpc.newBuilder().setCep("14090090").setCity("São Paulo").setState("São Paulo").setNumber("999")
                .setExtension("").build())
            .build()

        assertThrows<StatusRuntimeException> {
            grpcClient.create(request)
        }.run{
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("document: Document invalid", status.description)
        }
    }




    @MockBean(AnalysisClient::class)
    internal fun mockAnalysisService(): AnalysisClient? {
        return Mockito.mock(AnalysisClient::class.java)
    }

    @Factory
    internal class GrpcFactory {
        @Singleton
        fun generateCreateProposalGrpcClient(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                CreateProposalGrpc.CreateProposalBlockingStub = CreateProposalGrpc.newBlockingStub(channel)

    }
}