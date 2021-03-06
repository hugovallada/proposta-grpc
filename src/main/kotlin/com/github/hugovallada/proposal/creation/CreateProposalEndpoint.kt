package com.github.hugovallada.proposal.creation

import com.github.hugovallada.CreateProposalGrpc
import com.github.hugovallada.NewProposalGrpcRequest
import com.github.hugovallada.NewProposalGrpcResponse
import com.github.hugovallada.shared.extension.isValid
import com.github.hugovallada.shared.extension.toModel
import com.github.hugovallada.shared.interceptor.InterceptGrpc
import io.grpc.stub.StreamObserver
import javax.inject.Singleton
import javax.validation.Validator

@Singleton
@InterceptGrpc
class CreateProposalEndpoint(private val proposalService: CreateProposalService, val validator: Validator) :
    CreateProposalGrpc.CreateProposalImplBase() {
    override fun create(request: NewProposalGrpcRequest, responseObserver: StreamObserver<NewProposalGrpcResponse>) {
        proposalService.create(request.toModel().isValid(validator)).run {
            responseObserver.onNext(NewProposalGrpcResponse.newBuilder().setIdProposal(externalId.toString()).build())
        }
        responseObserver.onCompleted()
    }
}