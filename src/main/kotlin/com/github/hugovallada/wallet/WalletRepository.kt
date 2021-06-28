package com.github.hugovallada.wallet

import io.micronaut.data.annotation.Query
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface WalletRepository: JpaRepository<Wallet, Long> {

    @Query("SELECT * FROM tb_wallet  where email = :email and name = :name", nativeQuery = true)
    fun findWallet(email: String, name: String) : Wallet?
}