package com.qzl.cloudalbum.internet

import com.qzl.cloudalbum.other.UserHelper
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface CldAbService {

    //1检查当前的登录状态
    @GET("session-status")
    fun sessionStatus(
        @Header("Cookie") cookie: String? = UserHelper.getCookie()
    ): Call<MyResult<Boolean>>

    //2登入
    @POST("sign-in")
    @FormUrlEncoded
    fun loginForm(
        @Field("uid") userEmailAddress: String,
        @Field("paswd") userPassword: String
    ): Call<MyResult<Boolean>>

    //3注册
    @POST("sign-up")
    @FormUrlEncoded
    fun register(
        @Field("uid") userEmailAddress: String,
        @Field("paswd") userPassword: String,
        @Field("name") userName: String
    ): Call<MyResult<Boolean>>

    //4檢查郵箱是否已經被使用
    @POST("check-email")
    @FormUrlEncoded
    fun register(
        @Field("email") emailAddress: String
    ): Call<MyResult<Boolean>>

    //5改密码
    //如果新舊密碼一致或者原密碼錯誤將無法更改
    @POST("reset-paswd")
    @FormUrlEncoded
    fun reSetPassword(
        @Field("old") userOldPassword: String,
        @Field("new") userNewPassword: String,
        @Header("Cookie") cookie: String? = UserHelper.getCookie()
    ): Call<MyResult<Boolean>>


    //6获取文件信息
    @GET("file")
    fun getFileItem(
        @Query("path") targetPath: String,
        @Query("withHidden") ignoreHiddenItems: Boolean = UserHelper.getShowHidden(),
        @Header("Cookie") cookie: String? =UserHelper.getCookie()
    ): Call<MyResult<MyItem>>


    //7上传文件
    @POST("file")
    @Multipart
    fun upload(
        @Query("path") fileUploadDirPath: String,
        @Part fileToUpload: MultipartBody.Part,
        @Header("Cookie") cookie: String? =UserHelper.getCookie()
    ): Call<MyResult<Boolean>>

    //8删除文件
    @DELETE("file")
    fun delete(
        @Query("path") targetPathToRemove: String,
        @Query("paswd") userPassword: String? = "123456",
        @Header("Cookie") cookie: String? =UserHelper.getCookie(),
        @Query("removeTargetDir") removeTargetDir: Boolean? = true
    ): Call<MyResult<Boolean>>

    //9新建文件夹
    @POST("dir")
    fun newBuild(
        @Query("path") pathToCreate: String,
        @Query("hidden") createHiddenDir:Boolean,
        @Header("Cookie") cookie: String? = UserHelper.getCookie()
    ): Call<MyResult<Boolean>>

    //10文件重命名
    @POST("rename")
    fun rename(
        @Query("oldPath") targetFileOrDirPath: String,
        @Query("newName") newNameOfTargetFileOrDir: String,
        @Header("Cookie") cookie: String? = UserHelper.getCookie()
    ): Call<MyResult<Boolean>>

    //11改变给定文件夹或者文件隐藏状态
    @POST("file-status")
    fun changeHideStatus(
        @Query("path") targetFileTochangeStatus: String,
        @Header("Cookie") cookie: String? = UserHelper.getCookie()
    ): Call<MyResult<Boolean>>

    //12恢复浅层删除的文件或者文件夹
    //不会恢复子文件夹和文件
    @POST("restore-file")
    fun restore(
        @Query("path") targetFileTochangeStatus: String,
        @Header("Cookie") cookie: String? = UserHelper.getCookie()
    ): Call<MyResult<Boolean>>

    //13获取name email
    @GET("user/base")
    fun baseInfo(
        @Header("Cookie") cookie: String? = UserHelper.getCookie()
    ): Call<MyResult<MyUser>>

    //14获取用户扩展信息
    //容量 是否验证
    @GET("user/information")
    fun userInfo(
        @Header("Cookie") cookie: String? = UserHelper.getCookie()
    ): Call<MyResult<MyUserInformation>>

    //15获取头像信息
    @GET("user/image")
    fun userHeadPic(
        @Header("Cookie") cookie: String? = UserHelper.getCookie()
    ): Call<MyResult<MyUserImage>>

    //16上传头像
    @POST("user/head")
    @Multipart
    fun uploadHeadPic(
        @Part fileToUpload: MultipartBody.Part,
        @Header("Cookie") cookie: String? =UserHelper.getCookie()
    ): Call<MyResult<Boolean>>

}