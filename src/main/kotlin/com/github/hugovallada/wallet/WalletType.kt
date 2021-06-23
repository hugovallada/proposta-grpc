package com.github.hugovallada.wallet

import java.lang.IllegalArgumentException

enum class WalletType {
    PAYPALL,
    SAMSUNG;

    companion object {
        fun of(name: String): WalletType {
            val names = listOf<String>("PAYPALL","SAMSUNG")
            if (names.contains(name.toUpperCase())) {
                return WalletType.valueOf(name.toUpperCase())
            }

            throw IllegalArgumentException("Wallet type not found")
        }
    }
}