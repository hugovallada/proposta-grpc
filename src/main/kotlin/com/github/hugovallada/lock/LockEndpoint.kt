package com.github.hugovallada.lock

import com.github.hugovallada.CardLockGrpc
import com.github.hugovallada.CardLockGrpc.CardLockImplBase
import com.github.hugovallada.LockGrpcRequest
import com.github.hugovallada.LockGrpcResponse
import com.github.hugovallada.credit_card.CreditCardRepository
import com.github.hugovallada.shared.extension.isValid
import com.github.hugovallada.shared.extension.toModel
import com.github.hugovallada.shared.interceptor.InterceptGrpc
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@Singleton
@InterceptGrpc
class LockEndpoint(
    @Inject private val lockService: LockService,
    @Inject private val validator: Validator,
    @Inject private val creditCardRepository: CreditCardRepository
) : CardLockImplBase() {

    override fun lock(request: LockGrpcRequest, responseObserver: StreamObserver<LockGrpcResponse>) {
        lockService.lock(request.toModel(creditCardRepository).isValid(validator)).run {
            val message = if(this){
                "Credit card ${request.cardNumber} has been locked"
            }else{
                "Credit card ${request.cardNumber} hasn't been locked"
            }

            responseObserver.onNext(LockGrpcResponse.newBuilder().setMessage(message).build())
            responseObserver.onCompleted()
        }
    }
}