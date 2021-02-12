package com.qzl.cloudalbum.internet

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("test-login/json")
    fun loginPost(@Body data: IdPw): Call<LRData>
}