package com.github.hugovallada.shared.interceptor

import io.micronaut.aop.Around

@MustBeDocumented
@Retention
@Target(AnnotationTarget.CLASS)
@Around
annotation class InterceptGrpc()
