package com.qzl.cloudalbum.internet

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {

    /*
    sign-in 登录
    sign-up 注册
    sign-out 登出
    /file {
        get :获取指定文件或者文件夹
        post 上传文件到指定位置
        delete 删除文件 【/结尾清空文件夹，否则文件夹也会删除】
    }
    /walk 全部信息
     */

    private const val BASE_URL = "http://39.104.71.38:8080/"/*"http://192.168.31.186/"*/

    private val retrofit = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()).client(myClient()).build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    //取消重定向
    private fun myClient():OkHttpClient=OkHttpClient().newBuilder().followRedirects(false).build()
}