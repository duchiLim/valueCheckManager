package com.example.validation

@Target(AnnotationTarget.CLASS)
annotation class ValueCheck

@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD
)
annotation class RequiredValue


