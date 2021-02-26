package com.qzl.cloudalbum.internet

import com.qzl.cloudalbum.other.UserHelper
import okhttp3.MultipartBody
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

    //注册
    @POST("sign-up")
    @FormUrlEncoded
    fun register(
        @Field("uid") uid: String,
        @Field("paswd") pw: String,
        @Field("name") name: String
    ): Call<ResponseBody>

    //获取全部信息
    @GET("walk")
    fun getWalk(@Header("Cookie") cookie: String?): Call<ResponseBody>

    //文件信息
    @GET("file")
    fun getFileItem(
        @Query("path") path: String?,
        @Query("withHidden") withHidden: Boolean? = UserHelper.getShowHidden(),
        @Header("Cookie") cookie: String? = UserHelper.getCookie()
    ): Call<ResponseBody>

    //上传文件
    @POST("file")
    @Multipart
    fun realupload(
        @Query("path") targetPath: String?,
        @Part file: MultipartBody.Part?,
        @Header("Cookie") cookie: String? = UserHelper.getCookie()
    ): Call<ResponseBody>

    //新建文件夹
    @POST("dir")
    fun newBuild(
        @Query("path") pathToCreate: String,
        @Query("hidden") createHiddenDir: Boolean? = false,
        @Header("Cookie") cookie: String? = UserHelper.getCookie()
    ): Call<ResponseBody>

    //删除文件
    @DELETE("file")
    fun delete(
        @Query("path") path: String?,
        @Query("paswd") password: String? = UserHelper.getPassword(),
        @Query("removeTargetDir") removeTargetDir: Boolean? = true,
        @Header("Cookie") cookie: String? = UserHelper.getCookie()
    ): Call<ResponseBody>

}