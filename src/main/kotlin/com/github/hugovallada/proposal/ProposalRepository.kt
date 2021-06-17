package com.github.hugovallada.proposal

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface ProposalRepository : JpaRepository<Proposal, Long> {

    fun existsByDocument(document: String) : Boolean

    fun findByDocument(document: String) : Proposal?

    @Query("SELECT * from tb_proposal where status = 'ELIGIBLE' and credit_card_id is null", nativeQuery = true)
    fun proposalReadyForAssociation(): List<Proposal>
}