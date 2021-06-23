package com.github.hugovallada.wallet

import com.github.hugovallada.AssociateWalletGrpcRequest
import com.github.hugovallada.credit_card.CreditCardRepository
import com.github.hugovallada.shared.exception.TargetNotfoundException
import com.github.hugovallada.shared.exception.UnprocessableEntityException
import com.github.hugovallada.shared.extension.isValid
import com.github.hugovallada.shared.extension.toModel
import com.github.hugovallada.shared.external.credit_card.AssociateWalletClientRequest
import com.github.hugovallada.shared.external.credit_card.CreditCardClient
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Validator

@Singleton
class WalletAssociationService(
    @Inject private val validator: Validator,
    @Inject private val creditCardRepository: CreditCardRepository,
    @Inject private val creditCardClient: CreditCardClient,
    @Inject private val walletRepository: WalletRepository
) {


    fun associate(request: AssociateWalletGrpcRequest): String {
        val creditCard = creditCardRepository.findByNumber(request.cardNumber) ?: throw TargetNotfoundException("Couldn't find credit card with number ${request.cardNumber}")
        if(creditCard.locked) throw UnprocessableEntityException("The credit card is blocked")
        val wallet = request.toModel().isValid(validator)

        creditCard.wallets.filter {
            it.name == wallet.name
        }.run {
            if(size >= 1) throw UnprocessableEntityException("This credit card already has been associate with a ${wallet.name} wallet")
        }

        return try{
            val associate = creditCardClient.associate(
                creditCard.number,
                AssociateWalletClientRequest(wallet.email, wallet.name.toString())
            )

            val wal = walletRepository.findWallet(wallet.email, wallet.name.name) ?: walletRepository.save(wallet)
            creditCard.associateWallet(wal)
            creditCardRepository.update(creditCard)

            "Credit card ${creditCard.number} associated with wallet ${wallet.name}"
        }catch (exception: HttpClientResponseException){
            "Credit card ${creditCard.number} was not associated with wallet ${wallet.name}"
        }
    }

}