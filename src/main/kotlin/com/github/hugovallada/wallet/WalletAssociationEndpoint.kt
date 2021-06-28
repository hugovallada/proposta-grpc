package com.github.hugovallada.wallet

import com.github.hugovallada.AssociateWalletGrpc
import com.github.hugovallada.AssociateWalletGrpc.AssociateWalletImplBase
import com.github.hugovallada.AssociateWalletGrpcRequest
import com.github.hugovallada.AssociateWalletGrpcResponse
import com.github.hugovallada.shared.extension.isValid
import com.github.hugovallada.shared.extension.toModel
import com.github.hugovallada.shared.interceptor.InterceptGrpc
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@InterceptGrpc
class WalletAssociationEndpoint(@Inject private val service: WalletAssociationService) : AssociateWalletImplBase() {

    override fun associate(
        request: AssociateWalletGrpcRequest,
        responseObserver: StreamObserver<AssociateWalletGrpcResponse>
    ) {

        service.associate(request).run {
            responseObserver.onNext(AssociateWalletGrpcResponse.newBuilder()
                .setMessage(this).build())
            responseObserver.onCompleted()
        }



    }

}