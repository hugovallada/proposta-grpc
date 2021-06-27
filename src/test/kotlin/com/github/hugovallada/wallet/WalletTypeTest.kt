package com.github.hugovallada.wallet

import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

internal class WalletTypeTest{

    @Test
    internal fun `should return a valida wallet type when a value is passed`() {
        val value = WalletType.of("PAYPALL")

        value.shouldBeInstanceOf<WalletType>()
        value.shouldBe(WalletType.PAYPALL)
    }

    @Test
    internal fun `should throw an error when an invalid value is passed`() {
        assertThrows<IllegalArgumentException>{
            WalletType.of("CARRETO")
        }.run {
          message.shouldBe("Wallet type not found")
        }
    }
}