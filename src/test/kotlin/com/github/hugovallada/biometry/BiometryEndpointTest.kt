package com.github.hugovallada.biometry

import com.github.hugovallada.BiometryAssignGrpc
import com.github.hugovallada.BiometryAssignGrpc.BiometryAssignBlockingStub
import com.github.hugovallada.BiometryAssignGrpc.newBlockingStub
import com.github.hugovallada.BiometryGrpcRequest
import com.github.hugovallada.credit_card.CreditCard
import com.github.hugovallada.credit_card.CreditCardRepository
import com.github.hugovallada.credit_card.ExpirationDate
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.*
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class BiometryEndpointTest(
    private val creditCardRepository: CreditCardRepository,
    private val grpcClient: BiometryAssignBlockingStub,
    private val biometryRepository: BiometryRepository
){

    @BeforeEach
    internal fun setUp() {
        val creditCard = CreditCard(
            number = "6929-6084-4630-3370",
            issuedDate = LocalDateTime.now(),
            owner = "Hugo",
            creditLimit = "2500".toBigDecimal(),
            expirationDate = ExpirationDate(UUID.randomUUID().toString(),20, LocalDateTime.now())
        )

        creditCardRepository.save(creditCard)

    }

    @AfterEach
    internal fun tearDown() {
        biometryRepository.deleteAll()
        creditCardRepository.deleteAll()
    }

    @Test
    internal fun `should return the biometry external id when the assignment is completed`() {
        val fingerprint = "TWluaGEgRmluZ2VycHJpb250IHp1YWRhw6dh"
        val response = grpcClient.assign(
            BiometryGrpcRequest.newBuilder().setCardNumber("6929-6084-4630-3370")
                .setFingerPrint(fingerprint).build()
        )

        with(response){
            assertTrue(biometryRepository.existsByFingerPrint(fingerprint))
            assertTrue(response.id.isNotBlank())
            assertTrue(response.id.length == 36)
        }
    }

    @Test
    internal fun `should return status not found when the credit card number does not exists in the database`() {
        val cardNumber = "8282-1918-2929-2222"
        assertThrows<StatusRuntimeException>{
            grpcClient.assign(BiometryGrpcRequest.newBuilder().setFingerPrint("jidjiadjiahdhahduhad").setCardNumber(cardNumber).build())
        }.run {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Credit card with number $cardNumber not found", status.description)
        }
    }

    @Test
    internal fun `should return status invalid argument when the arguments send are invalid`() {
        val cardNumber = "6929-6084-4630-3370"
        assertThrows<StatusRuntimeException>{
            grpcClient.assign(BiometryGrpcRequest.newBuilder().setFingerPrint("").setCardNumber(cardNumber).build())
        }.run {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("fingerPrint: Should not be blank", status.description)
        }
    }

    @Test
    internal fun `shoud return status already exists when the biometry has already been assigned`() {
        val biometry = Biometry(creditCard = creditCardRepository.findByNumber("6929-6084-4630-3370")!!, fingerPrint = "TWluaGEgRmluZ2VycHJpb250IHp1YWRhw6dhc2RhZGFkYQ==")
        biometryRepository.save(biometry)

        assertThrows<StatusRuntimeException> {
            grpcClient.assign(BiometryGrpcRequest.newBuilder().setFingerPrint("TWluaGEgRmluZ2VycHJpb250IHp1YWRhw6dhc2RhZGFkYQ==").setCardNumber("6929-6084-4630-3370").build())
        }.run {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("This biometry has already been assigned to a credit card",status.description)
        }
    }

    @Factory
    class GrpcFactory{

        @Singleton
        fun generateBioemtryStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) = newBlockingStub(channel)
    }
}