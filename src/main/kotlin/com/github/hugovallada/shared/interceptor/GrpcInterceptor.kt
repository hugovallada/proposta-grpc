package com.github.hugovallada.shared.interceptor

import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(InterceptGrpc::class)
class GrpcInterceptor : MethodInterceptor<Any, Any> {
    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {
        return try {
            context.proceed()
        } catch (exception: Exception) {

            val response = context.parameterValues[1] as StreamObserver<*>

            when (exception) {
                is ConstraintViolationException -> Status.INVALID_ARGUMENT.withCause(exception)
                    .withDescription(exception.message)
                else -> Status.UNKNOWN.withCause(exception).withDescription("Unknow error ...")
            }.run {
                response.onError(StatusRuntimeException(this))
            }
        }
    }
}