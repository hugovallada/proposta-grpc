package com.github.hugovallada.address

import javax.persistence.*

@Entity
@Table(name = "tb_address")
class Address(
    val city: String,
    val state: String,
    val cep: String,
    val number: String,
    val extension: String
) {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
}