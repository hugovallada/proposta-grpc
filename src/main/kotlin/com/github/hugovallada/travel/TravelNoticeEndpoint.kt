package com.github.hugovallada.travel

import com.github.hugovallada.TravelNoticeGrpc
import com.github.hugovallada.TravelNoticeGrpcRequest
import com.github.hugovallada.TravelNoticeGrpcResponse
import com.github.hugovallada.shared.interceptor.InterceptGrpc
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@InterceptGrpc
class TravelNoticeEndpoint : TravelNoticeGrpc.TravelNoticeImplBase() {

    override fun notificate(
        request: TravelNoticeGrpcRequest,
        responseObserver: StreamObserver<TravelNoticeGrpcResponse>
    ) {



    }
}