package com.qzl.cloudalbum.internet

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {
    //http://39.104.71.38/test-login/json
    private const val BASE_URL = "http://39.104.71.38/"/*"http://192.168.31.186/"*/

    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
}