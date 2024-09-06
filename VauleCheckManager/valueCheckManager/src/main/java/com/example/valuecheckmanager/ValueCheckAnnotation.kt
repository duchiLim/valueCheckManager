package com.example.valuecheckmanager

@Target(AnnotationTarget.CLASS)
annotation class ValueCheck

@Target(
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD
)
annotation class RequiredValue


