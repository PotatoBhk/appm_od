package com.tesisunl.appod

data class System(
    val id: Int,
    val name: String,
    val cameras: Int,
    val link: String,
    val model: Int
)

data class ResponseLogin (
    val isValid : java.lang.Boolean,
    val message : String,
    val status: java.lang.Boolean
)
