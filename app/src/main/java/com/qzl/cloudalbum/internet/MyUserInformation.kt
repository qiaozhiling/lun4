package com.qzl.cloudalbum.internet

data class MyUserInformation(
    val totalSize: Long,
    val usedSize: Long,
    val formatTotalSize: String,
    val formatUsedSize: String,
    val verify: Boolean
)