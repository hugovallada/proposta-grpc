package com.github.hugovallada.address

import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity
@Table(name = "tb_address")
class Address(
    @field:NotBlank
    val city: String,
    @field:NotBlank
    val state: String,
    @field:NotBlank
    val cep: String,
    @field:NotBlank
    val number: String,
    @field:NotBlank
    val extension: String
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}