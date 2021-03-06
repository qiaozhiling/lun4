package com.qzl.cloudalbum.internet

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.qzl.cloudalbum.other.UserHelper
import com.qzl.cloudalbum.other.showToastOnUiLong
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.StringBuilder
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object NetHelper {

    //返回cookie状态
    //001 判断cookie是否过期
    @Throws(Exception::class)
    suspend fun cookieInDate(context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val result =
                    ServiceCreator.create(CldAbService::class.java).sessionStatus().await(context)
                result
            } catch (e: Exception) {
                throw e
            }
        }
    }

    //返回该级data信息
    //002获得文件信息
    @Throws(Exception::class)
    suspend fun getFile(path: String, context: Context): MyItem {
        return withContext(Dispatchers.IO) {
            try {
                val myResult =
                    ServiceCreator.create(CldAbService::class.java).getFileItem(path).await(context)
                myResult
            } catch (e: Exception) {
                throw e
            }
        }
    }

    // 返回是否成功
    //003登入
    @Throws(Exception::class)
    suspend fun login(id: String, password: String, sPf: SharedPreferences): Boolean {
        //在IO线程执行
        return withContext(Dispatchers.IO) {

            val service = ServiceCreator.create(CldAbService::class.java)
            try {
                val call = service.loginForm(id, password).clone()
                val response = call.execute()
                val result = response.body()
                //返回lambda表达式(?)
                if (!result?.err!!) {
                    //保存登入数据
                    with(sPf.edit()) {
                        UserHelper.setCookie(response.headers().get("Set-Cookie"))
                        UserHelper.setEmail(id)
                        UserHelper.setPassword(password)
                        putBoolean("isLogged", true)
                        putString("Cookie", response.headers().get("Set-Cookie"))
                        putString("id", id)
                        putString("password", password)
                        apply()
                    }
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                //抛出
                throw e
            }
        }
    }

    // 返回是否成功
    //004新建文件夹
    @Throws(Exception::class)
    suspend fun newBuildDir(pathToBuild: String, hidden: Boolean, context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val data =
                    ServiceCreator.create(CldAbService::class.java).newBuild(pathToBuild, hidden)
                        .await(context)
                data
            } catch (e: Exception) {
                throw e
            }
        }
    }

    // 返回是否成功
    //005上传图片
    @Throws(Exception::class)
    suspend fun uploadPic(parentPath: String, file: MultipartBody.Part, context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val data =
                    ServiceCreator.create(CldAbService::class.java).upload(parentPath, file)
                        .await(context)
                Log.i("NetHelper Upload", data.toString())
                data.first().data
            } catch (e: Exception) {
                throw e
            }
        }
    }

    //Throw 返回是否成功
    //006上传头像
    @Throws(Exception::class)
    suspend fun uploadHeadPic(file: MultipartBody.Part, context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val data =
                    ServiceCreator.create(CldAbService::class.java).uploadHeadPic(file)
                        .await(context)
                data
            } catch (e: Exception) {
                throw e
            }
        }
    }

    // 返回是否成功
    //007判断邮箱
    @Throws(Exception::class)
    suspend fun emailInLaw(email: String, context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val data =
                    ServiceCreator.create(CldAbService::class.java).register(email)
                        .await(context)
                data
            } catch (e: Exception) {
                throw e
            }
        }
    }

    //返回是否成功
    //008注册
    @Throws(Exception::class)
    suspend fun register(email: String, name: String, paswd: String, context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val data =
                    ServiceCreator.create(CldAbService::class.java).register(email, paswd, name)
                        .await(context)
                data
            } catch (e: Exception) {
                throw e
            }
        }
    }

    //Throw 返回用户
    //009 name eamil 信息
    @Throws(Exception::class)
    suspend fun getUserBaseInfo(context: Context): MyUser {
        return withContext(Dispatchers.IO) {
            try {
                val data =
                    ServiceCreator.create(CldAbService::class.java).baseInfo()
                        .await(context)
                data
            } catch (e: Exception) {
                throw e
            }
        }
    }

    //返回获取容量 验证状态 信息
    //010
    @Throws(Exception::class)
    suspend fun getUserInfo(context: Context): MyUserInformation {
        return withContext(Dispatchers.IO) {
            try {
                val data =
                    ServiceCreator.create(CldAbService::class.java).userInfo()
                        .await(context)
                data
            } catch (e: Exception) {
                throw e
            }
        }
    }

    //返回UserImage路径数据
    //011获取头像头像图片路径信息
    @Throws(Exception::class)
    suspend fun getHeadPic(context: Context): MyUserImage {
        return withContext(Dispatchers.IO) {
            try {
                val data =
                    ServiceCreator.create(CldAbService::class.java).userHeadPic()
                        .await(context)
                data
            } catch (e: Exception) {
                throw e
            }
        }
    }

    //返回成功信息
    //012用户名称重命名
    @Throws(Exception::class)
    suspend fun reUserName(newName: String, context: Context): String {
        return withContext(Dispatchers.IO) {
            try {
                val data =
                    ServiceCreator.create(CldAbService::class.java).userRename(newName)
                        .await(context)
                data
            } catch (e: Exception) {
                throw e
            }
        }
    }

    // 返回是否成功
    //013验证邮箱
    @Throws(Exception::class)
    suspend fun verifyEmail(code: String?, context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val result =
                    if (code == null) ServiceCreator.create(CldAbService::class.java).verify()
                        .await(context)
                    else ServiceCreator.create(CldAbService::class.java).verify(code).await(context)

                result
            } catch (e: Exception) {
                throw e
            }
        }
    }

    // 返回是否成功
    //014发送找回密码code
    @Throws(Exception::class)
    suspend fun findPasword(email: String, context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val result =
                    ServiceCreator.create(CldAbService::class.java).findPw(email).await(context)

                result
            } catch (e: Exception) {
                throw e
            }
        }
    }

    // 返回是否成功
    //014发送找回密码code
    @Throws(Exception::class)
    suspend fun findPasword(email: String, code: String, paswd: String, context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val result =
                    ServiceCreator.create(CldAbService::class.java).findPw(email, code, paswd)
                        .await(context)

                result
            } catch (e: Exception) {
                throw e
            }
        }
    }

    // 返回是否成功
    //015重置密碼
    @Throws(Exception::class)
    suspend fun resetPW(old: String, new: String, context: Context): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val result =
                    ServiceCreator.create(CldAbService::class.java).reSetPassword(old, new)
                        .await(context)

                result
            } catch (e: Exception) {
                throw e
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////

    // 网络请求
    @Throws(Exception::class)
    suspend fun <T> Call<MyResult<T>>.await(context: Context): T {
        return suspendCoroutine {

            this.enqueue(object : Callback<MyResult<T>> {
                override fun onFailure(call: Call<MyResult<T>>, t: Throwable) {
                    Log.e("await Exception", "${t.javaClass.name}-----${t.message}")
                    it.resumeWithException(t)
                }

                override fun onResponse(
                    call: Call<MyResult<T>>,
                    response: Response<MyResult<T>>
                ) {
                    try {
                        val body = response.body()
                        if (response.code() != 200) {
                            throw IOException("code !=200")
                        } else if (body == null) {
                            throw IOException("body == null")
                        } else {
                            if (body.err) {
                                GlobalScope.launch {
                                    //弹出message body.err
                                    body.message.replace("|", "\n").showToastOnUiLong(context)
                                }
                                throw IOException("err")
                            } else {
                                it.resume(body.data)
                            }
                        }

                    } catch (e: Exception) {
                        Log.e("await Exception", "${e.javaClass.name}-----${e.message}")
                        it.resumeWithException(e)
                    }
                }
            })
        }
    }
}