package com.github.hugovallada.travel

import com.github.hugovallada.TravelNoticeGrpc
import com.github.hugovallada.TravelNoticeGrpcRequest
import com.github.hugovallada.TravelNoticeGrpcResponse
import com.github.hugovallada.shared.interceptor.InterceptGrpc
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@InterceptGrpc
class TravelNoticeEndpoint(@Inject private val travelNoticeService: TravelNoticeService) : TravelNoticeGrpc.TravelNoticeImplBase() {

    override fun notificate(
        request: TravelNoticeGrpcRequest,
        responseObserver: StreamObserver<TravelNoticeGrpcResponse>
    ) {

        travelNoticeService.notificate(request).run {
            responseObserver.onNext(TravelNoticeGrpcResponse.newBuilder().setMessage(this).build())
        }
        responseObserver.onCompleted()
    }
}