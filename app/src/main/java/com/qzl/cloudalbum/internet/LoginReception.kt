package com.qzl.cloudalbum.internet

import java.io.Serializable

data class LoginReception(val date: Boolean, val err: Boolean, val message: String) : Serializable