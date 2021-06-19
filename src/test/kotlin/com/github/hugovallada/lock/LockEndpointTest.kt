package com.github.hugovallada.lock

import com.github.hugovallada.CardLockGrpc
import com.github.hugovallada.LockGrpcRequest
import com.github.hugovallada.credit_card.CreditCard
import com.github.hugovallada.credit_card.CreditCardRepository
import com.github.hugovallada.credit_card.ExpirationDate
import com.github.hugovallada.shared.exception.UnprocessableEntityException
import com.github.hugovallada.shared.external.credit_card.CreditCardClient
import com.github.hugovallada.shared.external.credit_card.LockCreditCardClientResponse
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class LockEndpointTest(
    @Inject private val grpcClient: CardLockGrpc.CardLockBlockingStub,
    @Inject private val creditCardRepository: CreditCardRepository,
    @Inject private val lockRepository: LockRepository
){
    @Inject
    lateinit var creditCardClient: CreditCardClient



    @BeforeEach
    internal fun setUp() {
        val card = CreditCard("2938-4620-3045-9042", LocalDateTime.now(),"Hugo", BigDecimal(2500), ExpirationDate(UUID.randomUUID().toString(),20,
            LocalDateTime.now()))

        val lockedCard = CreditCard("2938-4620-3045-9041", LocalDateTime.now(),"Hugo", BigDecimal(2500), ExpirationDate(UUID.randomUUID().toString(),20,
            LocalDateTime.now()))
        lockedCard.locked = true

        creditCardRepository.save(card)
        creditCardRepository.save(lockedCard)
    }

    @AfterEach
    internal fun tearDown() {
        lockRepository.deleteAll()
        creditCardRepository.deleteAll()
    }

    @Test
    internal fun `should return not found when the credit card number does not exists in the database`() {
        assertThrows<StatusRuntimeException>{
            grpcClient.lock(LockGrpcRequest.newBuilder().setCardNumber("98292929292").build())
        }.run {
            assertEquals(Status.NOT_FOUND.code,status.code)
            assertEquals("There's no credit card with number 98292929292", status.description)
        }
    }

    @Test
    internal fun `should return invalid argument when an invalid argument is send`() {
        assertThrows<StatusRuntimeException>{
            grpcClient.lock(LockGrpcRequest.newBuilder().setCardNumber("2938-4620-3045-9042").build())
        }.run {
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
        }
    }


    @Test
    internal fun `should return already exists when an already locked credit card is send`() {
        assertThrows<StatusRuntimeException> {
            grpcClient.lock(LockGrpcRequest.newBuilder().setCardNumber("2938-4620-3045-9041").setClientIp("0.0.0.0").setUserAgent("firewolf").build())
        }.run {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
        }
    }

    @Test
    internal fun `should block the card and return a sucess message`() {
        Mockito.`when`(creditCardClient.lockCreditCard("2938-4620-3045-9042", "grpc"))
            .thenReturn(HttpResponse.ok(LockCreditCardClientResponse(resultado = "BLOQUEADO")))

        val response = grpcClient.lock(LockGrpcRequest.newBuilder().setCardNumber("2938-4620-3045-9042").setClientIp("0.0.0.0").setUserAgent("firewolf").build())
        with(response){
            assertTrue(creditCardRepository.findByNumber("2938-4620-3045-9042")!!.locked)
            assertEquals("Credit card 2938-4620-3045-9042 has been locked", response.message)
        }
    }

    @Test
    internal fun `should not block the card and return a failure message`() {
        Mockito.`when`(creditCardClient.lockCreditCard("2938-4620-3045-9042", "grpc"))
            .thenThrow(HttpClientResponseException::class.java)

        val response = grpcClient.lock(LockGrpcRequest.newBuilder().setCardNumber("2938-4620-3045-9042").setClientIp("0.0.0.0").setUserAgent("firewolf").build())
        with(response){
            //assertFalse(creditCardRepository.findByNumber("2938-4620-3045-9042")!!.locked)
            assertEquals("Credit card 2938-4620-3045-9042 hasn't been locked", response.message)
        }
    }

    @MockBean(CreditCardClient::class)
    fun mockClient(): CreditCardClient? {
        return Mockito.mock(CreditCardClient::class.java)
    }

    @Factory
    internal class FactoryGrpc{
        @Singleton
        fun generateLockStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) = CardLockGrpc.newBlockingStub(channel)
    }
}