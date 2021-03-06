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
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.every
import io.mockk.mockk
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
            .setAddress(AddressGrpc.newBuilder().setCep("14090090").setCity("S??o Paulo").setState("S??o Paulo").setNumber("999")
                .setExtension("").build())
            .build()

        every {
            analysisClient.analyze(AnalysisProposalRequest("86852124053","Hugo","5"))
        } returns HttpResponse.created(AnalysisProposalResponse("86852124053","Hugo","SEM_RESTRICAO","1"))

        val response = grpcClient.create(request)
        with(response){
            idProposal.length.shouldBeExactly(36)
            proposalRepository.existsByDocument("86852124053").shouldBeTrue()
        }
    }

    @Test
    internal fun `should return an unknown status when something unknown happens`(){
        val request = NewProposalGrpcRequest.newBuilder()
            .setDocument("86852124053")
            .setEmail("email@email.com")
            .setName("Hugo")
            .setSalary("2500")
            .setAddress(AddressGrpc.newBuilder().setCep("14090090").setCity("S??o Paulo").setState("S??o Paulo").setNumber("999")
                .setExtension("").build())
            .build()

        every {
            analysisClient.analyze(AnalysisProposalRequest("86852124053","Hugo","2"))
        } returns HttpResponse.badRequest()

        val response = assertThrows<StatusRuntimeException> {
            grpcClient.create(request)
        }
        with(response){
            status.code.shouldBe(Status.UNKNOWN.code)
        }
    }

    @Test
    internal fun `should create a new proposal in the database and return it's external id when it's not eligible`(){
        val request = NewProposalGrpcRequest.newBuilder()
            .setDocument("32605826066")
            .setEmail("email@email.com")
            .setName("Hugo")
            .setSalary("2500")
            .setAddress(AddressGrpc.newBuilder().setCep("14090090").setCity("S??o Paulo").setState("S??o Paulo").setNumber("999")
                .setExtension("").build())
            .build()

        every {
            analysisClient.analyze(AnalysisProposalRequest("32605826066","Hugo","2"))
        } returns (HttpResponse.unprocessableEntity())

        val response = grpcClient.create(request)
        with(response){
            idProposal.length.shouldBeExactly(36)
            proposalRepository.existsByDocument("32605826066")
            proposalRepository.findByDocument("32605826066")?.status.shouldBe(ProposalStatus.NOT_ELIGIBLE)
        }
    }



    @Test
    internal fun `should return status already exists when someone tries to create a proposal with an already existing document`(){
        val proposal = Proposal("32605826066","email@email.com","Hugo", Address("Itagua","S??o Paulo", "14999999","888",""),
            BigDecimal(2500)
        )
        proposalRepository.save(proposal)

        val request = NewProposalGrpcRequest.newBuilder()
            .setDocument("32605826066")
            .setEmail("email@email.com")
            .setName("Hugo")
            .setSalary("2500")
            .setAddress(AddressGrpc.newBuilder().setCep("14090090").setCity("S??o Paulo").setState("S??o Paulo").setNumber("999")
                .setExtension("").build())
            .build()

        assertThrows<StatusRuntimeException> {
            grpcClient.create(request)
        }.run {
            status.code.shouldBe(Status.ALREADY_EXISTS.code)
            status.description.shouldBe("There's already a proposal with this document")
        }
    }


    @Test
    internal fun `should return status invalid argument if validation fails`() {
        val request = NewProposalGrpcRequest.newBuilder()
            .setDocument("9090")
            .setEmail("email@email.com")
            .setName("Hugo")
            .setSalary("2500")
            .setAddress(AddressGrpc.newBuilder().setCep("14090090").setCity("S??o Paulo").setState("S??o Paulo").setNumber("999")
                .setExtension("").build())
            .build()

        assertThrows<StatusRuntimeException> {
            grpcClient.create(request)
        }.run{
            status.code.shouldBe(Status.INVALID_ARGUMENT.code)
            status.description.shouldBe("document: Document invalid")
        }
    }

    @Test
    internal fun `should return status unknown when there's an error within the external client`() {
        val request = NewProposalGrpcRequest.newBuilder()
            .setDocument("23509040082")
            .setEmail("email@email.com")
            .setName("Hugo")
            .setSalary("2500")
            .setAddress(AddressGrpc.newBuilder().setCep("14090090").setCity("S??o Paulo").setState("S??o Paulo").setNumber("999")
                .setExtension("").build())
            .build()

        every {
            analysisClient.analyze(AnalysisProposalRequest("23509040082","Hugo","4"))
        } returns HttpResponse.badRequest()

        val response = assertThrows<StatusRuntimeException> {  grpcClient.create(request) }
        with(response){
            status.code.shouldBe(Status.UNKNOWN.code)
        }
    }

    @MockBean(AnalysisClient::class)
    internal fun mockAnalysisService(): AnalysisClient = mockk()

    @Factory
    internal class GrpcFactory {
        @Singleton
        fun generateCreateProposalGrpcClient(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
                CreateProposalGrpc.CreateProposalBlockingStub = CreateProposalGrpc.newBlockingStub(channel)

    }
}