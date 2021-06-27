package com.github.hugovallada.proposal.creation

import com.github.hugovallada.proposal.Proposal
import com.github.hugovallada.proposal.ProposalRepository
import com.github.hugovallada.proposal.ProposalStatus
import com.github.hugovallada.shared.exception.DuplicateValueException
import com.github.hugovallada.shared.external.analysis.AnalysisClient
import com.github.hugovallada.shared.external.analysis.AnalysisProposalRequest
import com.github.hugovallada.shared.external.analysis.AnalysisProposalResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
class CreateProposalService(
    @Inject private val proposalRepository: ProposalRepository,
    @Inject private val analysisClient: AnalysisClient
) {


//    fun create(proposal: Proposal): Proposal {
//        if(proposalRepository.existsByDocument(proposal.document)) throw DuplicateValueException("There's already 1 proposal with this document")
//        proposalRepository.save(proposal).run {
//            status = try{
//                analysisClient.analyze(AnalysisProposalRequest(this))
//                ProposalStatus.ELIGIBLE
//            }catch (exception: HttpClientResponseException){
//                ProposalStatus.NOT_ELIGIBLE
//            }
//            return proposalRepository.update(this)
//        }
//    }

    fun create(proposal: Proposal) : Proposal {
        if(proposalRepository.existsByDocument(proposal.document)) throw DuplicateValueException("There's already a proposal with this document")
        proposalRepository.save(proposal).run {
            analysisClient.analyze(AnalysisProposalRequest(this)).let {
                status = when{
                    it.status == HttpStatus.CREATED -> ProposalStatus.ELIGIBLE
                    it.status == HttpStatus.UNPROCESSABLE_ENTITY -> ProposalStatus.NOT_ELIGIBLE
                    else -> throw HttpClientResponseException("", HttpResponse.badRequest(""))
                }

                return proposalRepository.update(this)
            }
        }
    }
}