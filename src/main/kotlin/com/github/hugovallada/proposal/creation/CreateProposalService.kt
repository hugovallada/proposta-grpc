package com.github.hugovallada.proposal.creation

import com.github.hugovallada.proposal.Proposal
import com.github.hugovallada.proposal.ProposalRepository
import com.github.hugovallada.shared.exception.DuplicateValueException
import javax.inject.Singleton

@Singleton
class CreateProposalService(private val proposalRepository: ProposalRepository) {

    fun create(proposal: Proposal): Proposal {
        if(proposalRepository.existsByDocument(proposal.document)) throw DuplicateValueException("There's already 1 proposal with this document")
        return proposalRepository.save(proposal)
    }
}