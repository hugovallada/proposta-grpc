package com.github.hugovallada.shared.interceptor

import com.github.hugovallada.shared.exception.DuplicateValueException
import com.github.hugovallada.shared.exception.TargetNotfoundException
import com.github.hugovallada.shared.exception.UnprocessableEntityException
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
                is DuplicateValueException -> Status.ALREADY_EXISTS.withCause(exception)
                    .withDescription(exception.message)
                is TargetNotfoundException -> Status.NOT_FOUND.withCause(exception)
                    .withDescription(exception.message)
                is UnprocessableEntityException -> Status.FAILED_PRECONDITION.withCause(exception)
                    .withDescription(exception.message)
                is IllegalArgumentException -> Status.INVALID_ARGUMENT.withCause(exception)
                    .withDescription(exception.message)
                else -> Status.UNKNOWN.withCause(exception).withDescription("Unknow error ...")
            }.run {
                response.onError(StatusRuntimeException(this))
            }
        }
    }
}