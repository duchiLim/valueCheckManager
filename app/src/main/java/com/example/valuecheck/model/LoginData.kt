package com.example.valuecheck.model

import com.example.validation.RequiredValue
import com.example.validation.ValueCheck

@ValueCheck
class LoginData {
    @RequiredValue
    var loginToken: String? = null
    @RequiredValue
    var member: MemberData? = null
}