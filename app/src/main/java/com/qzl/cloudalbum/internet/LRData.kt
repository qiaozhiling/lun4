package com.qzl.cloudalbum.internet

import java.io.Serializable

data class LRData(val date: String, val err: Boolean, val message: String) : Serializable