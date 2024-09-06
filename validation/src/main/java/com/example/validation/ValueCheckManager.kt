package com.example.validation

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.superclasses

class ValueCheckManager(
    private val parseData: Any?,
    private val jsonData: String?,
    private var apiUrl: String? = null,
    private var responseBody: ByteArray?,
    private var apiMethod: Int,
    private val statusCode: Int,
    private var reason: String
) {
    private val TAG = "ValueCheckManager"

    class Builder {
        private var parseData: Any? = null
        private var jsonData: String? = null
        private var apiUrl: String? = null
        private var responseBody: ByteArray? = null
        private var apiMethod: Int = 0
        private var statusCode: Int = 0
        private var reason: String = "lack of required keyPath"

        fun addParseData(parseData: Any?): Builder {
            this.parseData = parseData
            return this
        }

        fun addJsonData(jsonData: String): Builder {
            this.jsonData = jsonData
            return this
        }

        fun addApiUrl(apiUrl: String): Builder {
            this.apiUrl = apiUrl
            return this
        }

        fun addResponseBody(responseBody: ByteArray): Builder {
            this.responseBody = responseBody
            return this
        }

        fun addApiMethod(apiMethod: Int): Builder {
            this.apiMethod = apiMethod
            return this
        }

        fun addStatusCode(statusCode: Int): Builder {
            this.statusCode = statusCode
            return this
        }

        fun addReason(reason: String): Builder {
            this.reason = reason
            return this
        }

        fun build() = ValueCheckManager(
            parseData,
            jsonData,
            apiUrl,
            responseBody,
            apiMethod,
            statusCode,
            reason
        )
    }

    fun validate() {
        CoroutineScope(Dispatchers.IO).launch {
            if (parseData == null) {
                return@launch
            }

            val invalidKeys = ArrayList<String>()
            val keyPath = parseData.javaClass.simpleName
            try {
                validateInternal(parseData, invalidKeys, keyPath)
            } catch (e: Exception) {
                Log.e(TAG, "e : $e")
            }

            if (invalidKeys.size > 0) {
                // TODO: send log.
            }
        }
    }

    private fun validateInternal(value: Any, invalidKeys: MutableList<String>, keyPath: String) {
        // ValueCheck Annotation이 있는 경우만 체크
        if (value::class.findAnnotation<ValueCheck>() == null) {
            return
        }

        val properties = value::class.declaredMemberProperties.toMutableList()
        value::class.superclasses.forEach {
            properties.addAll(it.declaredMemberProperties)
        }

        properties.forEach {
            // Public이 아닌 경우 값을 읽어 올 수 없음.
            if (it.getter.visibility != KVisibility.PUBLIC) {
                return@forEach
            }

            val isRequiredData = it.findAnnotation<RequiredValue>() != null
            when (val annotationValue = it.getter.call(value)) {
                null -> {
                    if (isRequiredData) {
                        invalidKeys.add("$keyPath.${it.name}")
                    }
                }
                is String -> {
                    if (isRequiredData && annotationValue.isEmpty()) {
                        invalidKeys.add("$keyPath.${it.name}")
                    }
                }
                is Collection<*> -> {
                    annotationValue.forEachIndexed { index, entry ->
                        if (entry == null) {
                            if (isRequiredData) {
                                invalidKeys.add("$keyPath.${it.name}.$index")
                            }
                        } else {
                            validateInternal(entry, invalidKeys, "$keyPath.${it.name}.$index")
                        }
                    }
                }
                else -> {
                    if (!isPrimitiveType(annotationValue)) {
                        validateInternal(annotationValue, invalidKeys, "$keyPath.${it.name}")
                    }
                }
            }
        }
    }

    private fun isPrimitiveType(value: Any): Boolean {
        return (value is Boolean
            || value is Int
            || value is Long
            || value is Float
            || value is Double
            || value is String
            || value.javaClass.isPrimitive
        )
    }

}