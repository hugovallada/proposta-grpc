package com.github.hugovallada.address

import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "address")
class Address (
    val city: String,
        )