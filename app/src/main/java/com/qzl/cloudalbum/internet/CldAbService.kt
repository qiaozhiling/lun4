package com.qzl.cloudalbum.internet

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface CldAbService {
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

    //登入
    @POST("sign-in")
    @FormUrlEncoded
    fun login(@Field("uid") uid: String, @Field("paswd") pw: String): Call<LoginReception>

    //获取全部信息
    @GET("walk")
    fun getWalk(@Header("Cookie") cookie: String?): Call<ResponseBody>

    //文件信息
    @GET("file")
    fun getFileItem(
        @Header("Cookie") cookie: String? = null, @Query("path") path: String? = null
    ): Call<ResponseBody>

    //上传
    @Multipart
    @POST("file")
    fun upLoad(): Call<ResponseBody>


}