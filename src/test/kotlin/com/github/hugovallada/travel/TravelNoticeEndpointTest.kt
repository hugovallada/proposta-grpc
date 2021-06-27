package com.github.hugovallada.travel

import com.github.hugovallada.TravelNoticeGrpc
import com.github.hugovallada.TravelNoticeGrpc.TravelNoticeBlockingStub
import com.github.hugovallada.TravelNoticeGrpc.newBlockingStub
import com.github.hugovallada.TravelNoticeGrpcRequest
import com.github.hugovallada.credit_card.CreditCard
import com.github.hugovallada.credit_card.CreditCardRepository
import com.github.hugovallada.credit_card.ExpirationDate
import com.github.hugovallada.shared.external.credit_card.CreditCardClient
import com.github.hugovallada.shared.external.credit_card.TravelNoticeClientRequest
import com.github.hugovallada.shared.external.credit_card.TravelNoticeClientResponse
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class TravelNoticeEndpointTest(private val creditCardRepository: CreditCardRepository){

    @Inject
    lateinit var grpcClient: TravelNoticeBlockingStub

    @Inject
    lateinit var creditCardClient: CreditCardClient

    @BeforeEach
    internal fun setUp() {
        creditCardRepository.deleteAll()
    }

    @Test
    internal fun `should return status not found when the credit card number doesn't exist`() {
        assertThrows<StatusRuntimeException>{
        grpcClient.notificate(TravelNoticeGrpcRequest.newBuilder()
            .setCardNumber("1111-1111-1111-1111").build())
        }.run {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Couldn't find credit card with number 1111-1111-1111-1111", status.description)
        }
    }

    @Test
    internal fun `should return status invalid argument when data is invalid`() {
        val creditCard = CreditCard("9999-9999-9999-9999", LocalDateTime.now(),"Hugo", BigDecimal(2000),
            ExpirationDate("du8du8w", 25, LocalDateTime.now())
        )
        creditCardRepository.save(creditCard)

        assertThrows<StatusRuntimeException> {
            grpcClient.notificate(TravelNoticeGrpcRequest.newBuilder()
                .setCardNumber("9999-9999-9999-9999").setClientIp("firefox").setDestination("").setReturnDate("2020-05-09").build())
        }.run {
            status.code.shouldBe(Status.INVALID_ARGUMENT.code)
            status.description.shouldContain("returnDate: deve ser uma data futura")
            status.description.shouldContain("destination: não deve estar em branco")
        }
    }

    @Test
    internal fun `should return a success message when the travel notice is persisted`() {
        val creditCard = CreditCard("9999-9999-9999-9999", LocalDateTime.now(),"Hugo", BigDecimal(2000),
            ExpirationDate(UUID.randomUUID().toString(), 25, LocalDateTime.now())
        )
        creditCardRepository.save(creditCard)
        val destination = "SP"
        val returnDate =LocalDate.parse("2021-09-09")
        val request = TravelNoticeGrpcRequest.newBuilder()
            .setCardNumber(creditCard.number).setClientIp("0.0.0.0").setDestination(destination)
            .setUserAgent("foxfire").setReturnDate("2021-09-09").build()


        every {
            creditCardClient.notificate(creditCard.number, TravelNoticeClientRequest(destination,returnDate))
        } returns (Single.just(TravelNoticeClientResponse("CRIADO")))

        val response = grpcClient.notificate(request)
        with(response){
            message.shouldBe("Add travel for credit card number ${creditCard.number}")
        }
    }

    @Test
    internal fun `should return a failure message when the travel notice is persisted`() {
        val creditCard = CreditCard("9999-9999-9999-9999", LocalDateTime.now(),"Hugo", BigDecimal(2000),
            ExpirationDate(UUID.randomUUID().toString(), 25, LocalDateTime.now())
        )
        creditCardRepository.save(creditCard)
        val destination = "SP"
        val returnDate =LocalDate.parse("2021-09-09")
        val request = TravelNoticeGrpcRequest.newBuilder()
            .setCardNumber(creditCard.number).setClientIp("0.0.0.0").setDestination(destination)
            .setUserAgent("foxfire").setReturnDate("2021-09-09").build()

        every {
            creditCardClient.notificate(creditCard.number, TravelNoticeClientRequest(destination,returnDate))
        } throws HttpClientResponseException("", HttpResponse.badRequest(""))

        val response = grpcClient.notificate(request)
        with(response){
            message.shouldBe("Travel for credit card number ${creditCard.number} couldn't be saved")
        }
    }



    @MockBean(CreditCardClient::class)
    fun mockClient() : CreditCardClient = mockk()

    @Factory
    internal class GrpcFactory{
        @Singleton
        fun generateTestEndpointStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) = newBlockingStub(channel)
    }
}