package com.qzl.cloudalbum.other

import android.content.SharedPreferences
import android.util.Log
import com.qzl.cloudalbum.internet.LoginReception
import com.qzl.cloudalbum.internet.CldAbService
import okhttp3.ResponseBody
import retrofit2.Response
import java.lang.Exception
import java.net.ConnectException

object Helper {
    private var cookie: String? = null
    private var id: String? = null
    private var password: String? = null

    fun setCookie(cookie: String) {
        this.cookie = cookie
    }

    fun getCookie() = cookie

    fun setId(id: String) {
        this.id = id
    }

    fun getId() = id

    fun setPassword(password: String) {
        this.password = password
    }

    fun getPassword() = password

    fun login(
        service: CldAbService,
        id: String,
        password: String,
        sPf: SharedPreferences
    ): Boolean {
        var succeed = false
        val call = service.login(id, password)
        var response: Response<LoginReception>? = null

        val t = Thread {
            try {
                response = call.clone().execute()
            } catch (e: ConnectException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        t.start()
        t.join()

        response?.let {
            if (it.isSuccessful) {
                val reception = it.body()
                //危
                if (!reception!!.err) {
                    val headers = it.headers()
                    val cookie = headers.get("Set-Cookie") + ""
                    setCookie(cookie)
                    succeed = true

                    //存cookie id paswd
                    with(sPf.edit()) {
                        putString("Set-Cookie", getCookie())
                        putString("id", id)
                        putString("password", password)
                        apply()
                    }
                }
            }
        }
        Log.i("Login", "登入成功" + succeed.toString())
        return succeed
    }

    fun cookieInDate(service: CldAbService): Boolean {
        var succeed: Boolean = false
        val call = service.getWalk(cookie)
        var response: Response<ResponseBody>? = null

        val t = Thread {
            try {
                response = call.clone().execute()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        t.start()
        t.join()

        response?.let {
            val code = response?.code()
            if (code == 200) {
                succeed = true
            }
        }

        Log.i("Cookie", "cookie未过期" + succeed.toString())
        return succeed
    }
}