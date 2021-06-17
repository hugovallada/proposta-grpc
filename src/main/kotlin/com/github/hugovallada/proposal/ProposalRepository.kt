package com.github.hugovallada.proposal

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface ProposalRepository : JpaRepository<Proposal, Long> {

    fun existsByDocument(document: String) : Boolean
}