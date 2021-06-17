package com.github.hugovallada.proposal.detail

import com.github.hugovallada.ProposalStatusGrpc
import com.github.hugovallada.ProposalStatusGrpcRequest
import com.github.hugovallada.ProposalStatusGrpcResponse
import com.github.hugovallada.StatusProposal
import com.github.hugovallada.shared.interceptor.InterceptGrpc
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@InterceptGrpc
class ProposalDetailEndpoint(private val proposalDetailService: ProposalDetailService) : ProposalStatusGrpc.ProposalStatusImplBase() {
    override fun watch(
        request: ProposalStatusGrpcRequest,
        responseObserver: StreamObserver<ProposalStatusGrpcResponse>
    ) {
        proposalDetailService.watch(request.id).run {
            responseObserver.onNext(
                ProposalStatusGrpcResponse.newBuilder()
                    .setDocumet(document)
                    .setEmail(email)
                    .setName(name)
                    .setStatus(StatusProposal.valueOf(status.toString()))
                    .setCreditCard(creditCard?.number ?: "")
                    .build()
            )
            responseObserver.onCompleted()
        }


    }
}