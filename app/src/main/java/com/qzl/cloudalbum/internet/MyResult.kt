package com.qzl.cloudalbum.internet

data class MyResult<T>(
    val data: T,
    val err: Boolean,
    val message: String
)