package com.github.hugovallada.biometry

import com.github.hugovallada.shared.exception.DuplicateValueException
import javax.inject.Singleton

@Singleton
class BiometryService(private val biometryRepository: BiometryRepository) {
    fun assign(biometry: Biometry): Biometry {
        if(biometryRepository.existsByFingerPrint(biometry.fingerPrint)){
            throw DuplicateValueException("This biometry has already been assigned to a credit card")
        }
        biometryRepository.save(biometry).run {
            return biometry
        }
    }
}