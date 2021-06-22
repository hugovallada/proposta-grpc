package com.github.hugovallada.wallet

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface WalletRepository: JpaRepository<Wallet, Long> {
}