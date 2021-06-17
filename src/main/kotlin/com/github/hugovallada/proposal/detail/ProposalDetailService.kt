package com.github.hugovallada.proposal.detail

import com.github.hugovallada.proposal.ProposalRepository
import com.github.hugovallada.shared.exception.TargetNotfoundException
import java.util.*
import javax.inject.Singleton

@Singleton
class ProposalDetailService(private val proposalRepository: ProposalRepository) {

    fun watch(id: String) = proposalRepository.findByExternalId(UUID.fromString(id))
            ?: throw TargetNotfoundException("Can't find proposal with id: $id")



}