package com.example.valuecheck.model

import com.example.validation.RequiredValue
import com.example.validation.ValueCheck

@ValueCheck
data class MemberData(
    var memberNo: Int = 0,
    @RequiredValue var name: String? = null,
)