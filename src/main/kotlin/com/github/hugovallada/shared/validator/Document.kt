package com.github.hugovallada.shared.validator

import org.hibernate.validator.constraints.CompositionType
import org.hibernate.validator.constraints.CompositionType.OR
import org.hibernate.validator.constraints.ConstraintComposition
import org.hibernate.validator.constraints.br.CNPJ
import org.hibernate.validator.constraints.br.CPF
import javax.validation.Constraint
import javax.validation.ReportAsSingleViolation
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.reflect.KClass

@CPF
@CNPJ
@ConstraintComposition(OR)
@Retention(RUNTIME)
@MustBeDocumented
@ReportAsSingleViolation
@Target(FIELD)
@Constraint(validatedBy = [])
annotation class Document(
    val message: String = "Document invalid",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Any>> = []
)
