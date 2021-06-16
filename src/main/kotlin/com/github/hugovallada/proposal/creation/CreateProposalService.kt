package com.github.hugovallada.proposal.creation

import com.github.hugovallada.proposal.Proposal
import javax.inject.Singleton

@Singleton
class CreateProposalService {

    fun create(proposal: Proposal){
        println(proposal.externalId)
    }
}