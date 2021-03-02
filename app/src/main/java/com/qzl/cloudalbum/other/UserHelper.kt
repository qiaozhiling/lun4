package com.qzl.cloudalbum.other

import android.content.SharedPreferences
import android.util.Log
import com.qzl.cloudalbum.internet.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object UserHelper {
    private var cookie: String = ""
    private var email: String = ""
    private var password: String? = null
    private var showHidden: Boolean = false


    fun setCookie(cookie: String?) {
        cookie?.let {
            this.cookie = cookie
        }
    }


    fun getCookie() = cookie

    fun setEmail(email: String) {
        this.email = email
    }

    fun getEmail() = email

    fun setPassword(password: String?) {
        this.password = password
    }

    fun getPassword() = password

    fun getShowHidden() = showHidden

    fun setShowHidden(s: Boolean) {
        showHidden = s
    }

    //Throw 返回是否成功
    //判断邮箱
    suspend fun emailInLaw(email: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val myResult =
                    ServiceCreator.create(CldAbService::class.java).register(email)
                        .await()
                !myResult.err && myResult.data
            } catch (e: Exception) {
                throw e
            }
        }
    }

    //Throw 返回是否成功
    //注册
    suspend fun register(email: String, name: String, paswd: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val myResult =
                    ServiceCreator.create(CldAbService::class.java).register(email, paswd, name)
                        .await()
                !myResult.err
            } catch (e: Exception) {
                throw e
            }
        }
    }

    //Throw 返回是否成功
    //登入
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
                        setCookie(response.headers().get("Set-Cookie"))
                        setEmail(id)
                        setPassword(password)
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

    //Throw 返回该级data信息
    //获得文件信息
    suspend fun getFile(path: String): MyItem {
        return withContext(Dispatchers.IO) {
            try {
                val myResult =
                    ServiceCreator.create(CldAbService::class.java).getFileItem(path).await()
                val myItem = myResult.data
                if (!myResult.err) {
                    myItem
                } else {
                    throw IOException("err")
                }

            } catch (e: Exception) {
                throw e
            }
        }
    }

    //Throw 返回是否成功
    //新建文件夹
    suspend fun newBuildDir(pathToBuild: String, hidden: Boolean): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val myResult =
                    ServiceCreator.create(CldAbService::class.java).newBuild(pathToBuild, hidden)
                        .await()
                !myResult.err
            } catch (e: Exception) {
                throw e
            }
        }
    }

    //Throw 返回是否成功
    //重命名
    suspend fun reName(oldPath: String, newName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val myResult =
                    ServiceCreator.create(CldAbService::class.java).rename(oldPath, newName)
                        .await()
                !myResult.err
            } catch (e: Exception) {
                throw e
            }
        }
    }

    //Throw 返回是否成功
    //改变文件（夹）隐藏状态
    suspend fun changeStatus(targetFilePath: List<String>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val myResult =
                    ServiceCreator.create(CldAbService::class.java).changeHideStatus(targetFilePath)
                        .await()
                !myResult.err

            } catch (e: Exception) {
                throw e
            }
        }
    }

    //Throw 返回是否成功
    //删除文件（夹
    suspend fun deleteFile(pathToDelete: List<String>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val myResult =
                    ServiceCreator.create(CldAbService::class.java).delete(pathToDelete).await()
                Log.i("deeeeeeeeelte", myResult.toString())
                !myResult.err

            } catch (e: Exception) {
                throw e
            }
        }
    }

    //Throw 返回是否成功
    //上传
    suspend fun uploadPic(parentPath: String, file: MultipartBody.Part): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val myResult =
                    ServiceCreator.create(CldAbService::class.java).upload(parentPath, file)
                        .await()
                !myResult.err
            } catch (e: Exception) {
                throw e
            }
        }
    }

    //Throw 返回用户 name eamil 信息
    suspend fun getUserBaseInfo(): MyUser {
        return withContext(Dispatchers.IO) {
            try {
                val myResult =
                    ServiceCreator.create(CldAbService::class.java).baseInfo()
                        .await()
                if (myResult.err) throw RuntimeException("(myResult err)]]]")
                else myResult.data
            } catch (e: Exception) {
                throw e
            }
        }
    }

    //Throw 返回获取容量 验证状态 信息
    suspend fun getUserInfo(): MyUserInformation {
        return withContext(Dispatchers.IO) {
            try {
                val myResult =
                    ServiceCreator.create(CldAbService::class.java).userInfo()
                        .await()
                if (myResult.err) throw RuntimeException("(myResult err")
                else myResult.data
            } catch (e: Exception) {
                throw e
            }
        }
    }

    //Throw 返回UserImage路径数据
    //获取头像头像图片路径信息
    suspend fun getHeadPic(): MyUserImage {
        return withContext(Dispatchers.IO) {
            try {
                val myResult =
                    ServiceCreator.create(CldAbService::class.java).userHeadPic()
                        .await()
                if (myResult.err) throw RuntimeException("(myResult err")
                else myResult.data
            } catch (e: Exception) {
                throw e
            }
        }
    }


    //Throw 返回是否成功
    //判断cookie是否过期
    suspend fun cookieInDate(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val result = ServiceCreator.create(CldAbService::class.java).sessionStatus().await()
                val err = result.err
                val data = result.data
                !err && data
            } catch (e: Exception) {
                throw e
            }
        }
    }

    //Throw 返回是否成功
    //验证邮箱
    suspend fun verifyEmail(code: String?): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val result =
                    if (code == null) ServiceCreator.create(CldAbService::class.java).verify()
                        .await()
                    else ServiceCreator.create(CldAbService::class.java).verify(code).await()

                val err = result.err
                val data = result.data
                !err && data
            } catch (e: Exception) {
                throw e
            }
        }
    }

    //Throw
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine {
            enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    it.resumeWithException(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null && response.code() == 200) it.resume(body)
                    //响应码！=200cookie失效重定向
                    else if (response.code() != 200) it.resumeWithException(RuntimeException("Cookie失效"))
                    else if (body != null) it.resumeWithException(RuntimeException("body is null"))
                    else it.resumeWithException(RuntimeException("其他异常"))
                }
            })
        }
    }

    //文件名合法
    fun nameInLaw(name: String): Boolean =
        if (name == "") false else name.matches(Regex("^[^/\\\\?*<>:]+\$"))

}