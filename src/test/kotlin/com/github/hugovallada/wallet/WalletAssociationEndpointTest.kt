package com.github.hugovallada.wallet

import com.github.hugovallada.AssociateWalletGrpc
import com.github.hugovallada.AssociateWalletGrpc.AssociateWalletBlockingStub
import com.github.hugovallada.AssociateWalletGrpc.newBlockingStub
import com.github.hugovallada.AssociateWalletGrpcRequest
import com.github.hugovallada.credit_card.CreditCard
import com.github.hugovallada.credit_card.CreditCardRepository
import com.github.hugovallada.credit_card.ExpirationDate
import com.github.hugovallada.shared.external.credit_card.AssociateWalletClientRequest
import com.github.hugovallada.shared.external.credit_card.AssociateWalletClientResponse
import com.github.hugovallada.shared.external.credit_card.CreditCardClient
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainIgnoringCase
import io.kotest.matchers.string.shouldNotBeBlank
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkClass
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import java.lang.IllegalStateException
import java.lang.RuntimeException
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
@ExtendWith(MockKExtension::class)
internal class WalletAssociationEndpointTest(
    @Inject private val grpcClient: AssociateWalletBlockingStub,
    @Inject private val creditCardRepository: CreditCardRepository,
    @Inject private val walletRepository: WalletRepository
){
    @Inject
    lateinit var creditCardClient: CreditCardClient

    @Test
    internal fun `should return status not found when credit card number doesn't exist`() {
        assertThrows<StatusRuntimeException>{
            grpcClient.associate(AssociateWalletGrpcRequest.newBuilder().setCardNumber("0000-0000-0000-0000").build())
        }.run {
            status.code.shouldBe(Status.NOT_FOUND.code)
            status.description.shouldContain("Couldn't find credit card with number 0000-0000-0000-0000")
        }
    }

    @Test
    internal fun `should return failed precondition when credit card is locked`() {
        val creditCard = CreditCard("9282-2922-0882-0282", LocalDateTime.now(),"Hugo", BigDecimal(2500),
            ExpirationDate(UUID.randomUUID().toString(),25, LocalDateTime.now())
        )
        creditCard.locked = true
        creditCardRepository.save(creditCard)

        assertThrows<StatusRuntimeException> {
            grpcClient.associate(AssociateWalletGrpcRequest.newBuilder()
                .setCardNumber("9282-2922-0882-0282").setEmail("").setWallet("").build())
        }.run {
            status.code.shouldBe(Status.FAILED_PRECONDITION.code)
            status.description.shouldBe("The credit card is blocked")
        }
    }

    @Test
    internal fun `should return status unknown when something wrong happens`() {
        val creditCard = CreditCard("7297-2922-0882-0282", LocalDateTime.now(),"Hugo", BigDecimal(2500),
            ExpirationDate(UUID.randomUUID().toString(),25, LocalDateTime.now())
        )
        creditCardRepository.save(creditCard)


        every {
            creditCardClient.associate("7297-2922-0882-0282", AssociateWalletClientRequest("email@email.com","PAYPALL"))
        } throws RuntimeException("")


        assertThrows<StatusRuntimeException> {
            grpcClient.associate(AssociateWalletGrpcRequest.newBuilder()
                .setCardNumber("7297-2922-0882-0282").setEmail("email@email.com").setWallet("PAYPALL").build())
        }.run {
            status.description.shouldBe("Unknow error ...")
            status.code.shouldBe(Status.UNKNOWN.code)
        }

    }

    @Test
    internal fun `should return status invalid argument when data is invalid`() {
        val creditCard = CreditCard("9999-2922-0882-0282", LocalDateTime.now(),"Hugo", BigDecimal(2500),
            ExpirationDate(UUID.randomUUID().toString(),25, LocalDateTime.now())
        )
        creditCardRepository.save(creditCard)
        assertThrows<StatusRuntimeException> {
            grpcClient.associate(AssociateWalletGrpcRequest.newBuilder()
                .setCardNumber("9999-2922-0882-0282").setEmail("ktc2gmail.com").setWallet("PAYPALL").build())
        }.run {
            status.code.shouldBe(Status.INVALID_ARGUMENT.code)
            status.description.shouldContain("Invalid email")
        }
    }

    @Test
    internal fun `should return status invalid argument when wallet type is invalid`() {
        val creditCard = CreditCard("0999-2922-0882-0282", LocalDateTime.now(),"Hugo", BigDecimal(2500),
            ExpirationDate(UUID.randomUUID().toString(),25, LocalDateTime.now())
        )
        creditCardRepository.save(creditCard)
        assertThrows<StatusRuntimeException> {
            grpcClient.associate(AssociateWalletGrpcRequest.newBuilder()
                .setCardNumber("0999-2922-0882-0282").setEmail("ktc@gmail.com").setWallet("PAYPA").build())
        }.run {
            status.code.shouldBe(Status.INVALID_ARGUMENT.code)
            status.description.shouldContainIgnoringCase("Wallet Type not found")
        }
    }

    @Test
    internal fun `should return status already exists when credit card has already been associated with the same wallet`() {
        val creditCard = CreditCard("9997-2922-0882-0282", LocalDateTime.now(),"Hugo", BigDecimal(2500),
            ExpirationDate(UUID.randomUUID().toString(),25, LocalDateTime.now())
        )
        creditCardRepository.save(creditCard)

        val wallet = Wallet(WalletType.PAYPALL,"email@email.com")
        creditCard.associateWallet(wallet)
        creditCardRepository.update(creditCard)

        assertThrows<StatusRuntimeException> {
            grpcClient.associate(AssociateWalletGrpcRequest.newBuilder()
                .setWallet("PAYPALL").setEmail("email@email.com").setCardNumber("9997-2922-0882-0282").build())
        }.run {
            status.code.shouldBe(Status.ALREADY_EXISTS.code)
            status.description.shouldBe("This credit card already has been associate with a ${wallet.name} wallet")
        }
    }

    @Test
    internal fun `should return failure message when an exception occurs in the external client`() {
        val creditCard = CreditCard("9297-2922-0882-0282", LocalDateTime.now(),"Hugo", BigDecimal(2500),
            ExpirationDate(UUID.randomUUID().toString(),25, LocalDateTime.now())
        )
        creditCardRepository.save(creditCard)


        every {
            creditCardClient.associate("9297-2922-0882-0282", AssociateWalletClientRequest("email@email.com","PAYPALL"))
        } throws HttpClientResponseException("", HttpResponse.serverError(""))

        val grpcResponse = grpcClient.associate(AssociateWalletGrpcRequest.newBuilder()
            .setCardNumber("9297-2922-0882-0282").setEmail("email@email.com").setWallet("PAYPALL").build())

        with(grpcResponse){
            message.shouldNotBeBlank()
            message.shouldBe("Credit card 9297-2922-0882-0282 was not associated with wallet PAYPALL")
        }
    }

    @Test
    internal fun `should return success message when the credit card is associated`() {
        val creditCard = CreditCard("8297-2922-0882-0282", LocalDateTime.now(),"Hugo", BigDecimal(2500),
            ExpirationDate(UUID.randomUUID().toString(),25, LocalDateTime.now())
        )
        creditCardRepository.save(creditCard)


        every {
                creditCardClient.associate("8297-2922-0882-0282", AssociateWalletClientRequest("email@email.com", "PAYPALL"))
            }.returns(AssociateWalletClientResponse("ASSOCIADA",UUID.randomUUID().toString()))


        val grpcResponse = grpcClient.associate(AssociateWalletGrpcRequest.newBuilder()
            .setCardNumber("8297-2922-0882-0282").setEmail("email@email.com").setWallet("PAYPALL").build())

        with(grpcResponse){
            message.shouldNotBeBlank()
            message.shouldBe("Credit card 8297-2922-0882-0282 associated with wallet PAYPALL")
        }
    }

    @MockBean(CreditCardClient::class)
    fun mockkCreditCardClient() : CreditCardClient {
        return mockk()
    }

    @Factory
    internal class GrpcFactory{
        @Singleton
        fun grpcClient(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel) = newBlockingStub(channel)
    }
}