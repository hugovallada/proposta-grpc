package com.github.hugovallada.biometry

import com.github.hugovallada.BiometryAssignGrpc
import com.github.hugovallada.BiometryGrpcRequest
import com.github.hugovallada.BiometryGrpcResponse
import com.github.hugovallada.credit_card.CreditCardRepository
import com.github.hugovallada.shared.extension.isValid
import com.github.hugovallada.shared.extension.toModel
import com.github.hugovallada.shared.interceptor.InterceptGrpc
import io.grpc.stub.StreamObserver
import javax.inject.Singleton
import javax.validation.Validator

@Singleton
@InterceptGrpc
class BiometryEndpoint(
    private val biometryService: BiometryService,
    private val creditCardRepository: CreditCardRepository,
    private val validator: Validator
) : BiometryAssignGrpc.BiometryAssignImplBase() {

    override fun assign(request: BiometryGrpcRequest, responseObserver: StreamObserver<BiometryGrpcResponse>) {

        biometryService.assign(request.toModel(creditCardRepository).isValid(validator)).let {
            responseObserver.onNext(BiometryGrpcResponse.newBuilder().setId(it.externalId.toString()).build())
        }
        responseObserver.onCompleted()
    }

}