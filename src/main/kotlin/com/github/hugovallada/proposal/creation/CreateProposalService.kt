package com.github.hugovallada.proposal.creation

import com.github.hugovallada.proposal.Proposal
import com.github.hugovallada.proposal.ProposalRepository
import javax.inject.Singleton

@Singleton
class CreateProposalService(private val proposalRepository: ProposalRepository) {

    fun create(proposal: Proposal): Proposal {
        return proposalRepository.save(proposal)
    }
}