package com.github.hugovallada

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("com.github.hugovallada")
		.start()
}

