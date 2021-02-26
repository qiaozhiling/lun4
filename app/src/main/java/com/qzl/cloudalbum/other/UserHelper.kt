package com.qzl.cloudalbum.other

import android.content.SharedPreferences
import android.util.Log
import com.qzl.cloudalbum.internet.LoginReception
import com.qzl.cloudalbum.internet.CldAbService
import com.qzl.cloudalbum.internet.ServiceCreator
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.lang.Exception
import java.net.ConnectException

object UserHelper {
    private var cookie: String? = null
    private var id: String? = null
    private var password: String? = null
    private var showHidden: Boolean? = false

    fun setCookie(cookie: String?) {
        this.cookie = cookie
    }

    fun getCookie() = cookie

    fun setId(id: String?) {
        this.id = id
    }

    fun getId() = id

    fun setPassword(password: String?) {
        this.password = password
    }

    fun getPassword() = password

    fun getShowHidden() = showHidden

    fun setShowHidden(s: Boolean?) {
        showHidden = s
    }

    fun login(id: String, password: String, sPf: SharedPreferences): Boolean? {
        val service = ServiceCreator.create(CldAbService::class.java)
        var succeed = false
        val call = service.login(id, password)
        val response: Response<LoginReception>? = sendRequest(call)

        if (response != null) {
            if (response.isSuccessful) {
                val reception = response.body()
                //危
                if (!reception!!.err) {
                    val headers = response.headers()
                    val cookie = headers.get("Set-Cookie") + ""
                    setCookie(cookie)
                    succeed = true

                    //存cookie id paswd
                    with(sPf.edit()) {
                        putString("Cookie", getCookie())
                        putString("id", id)
                        putString("password", password)
                        putBoolean("isLogged", true)
                        apply()
                    }
                }
            }
        } else {
            return null
        }

        Log.i("Login", "登入成功" + succeed.toString())
        return succeed
    }

    //undone
    fun newBuildDir(pathToBuild: String): Boolean? {
        val service = ServiceCreator.create(CldAbService::class.java)
        val call = service.newBuild(pathToBuild)
        val response: Response<ResponseBody>? = sendRequest(call)

        if (response != null) {
            val json = response.body()?.string()
            val data = JSONObject(json).getBoolean("data")
            val err = JSONObject(json).getBoolean("err")
            return data && !err
        } else {
            return null
        }

    }

    //undone
    fun deleteFile(pathToDelete: String): Boolean? {
        val call = ServiceCreator.create(CldAbService::class.java).delete(pathToDelete)
        val response = sendRequest(call)
        if (response != null) {
            val json = response.body()?.string()
            val data = JSONObject(json).getBoolean("data")
            val err = JSONObject(json).getBoolean("err")
            return data && !err
        } else {
            return null
        }
    }

    fun cookieInDate(): Boolean? {
        val service = ServiceCreator.create(CldAbService::class.java)
        val call = service.getWalk(cookie)
        var response: Response<ResponseBody>? = null

        response = sendRequest(call)

        when {
            response == null -> return null
            response.code() == 200 -> return true
        }

        return false
    }

    fun getFile(path: String): MutableList<JSONObject>? {
        val service = ServiceCreator.create(CldAbService::class.java)
        val call = service.getFileItem(path)
        val response: Response<ResponseBody>? = sendRequest(call)

        when {
            response == null -> {
                Log.i("getFile", "网络链接失败")
                return null
            }

            response.code() == 200 -> {
                val json = response.body()!!.string()
                val data = JSONObject(json).getJSONObject("data")
                val jsonArray = data.getJSONArray("subItems")
                Log.i("getFile", "获取成功")
                return mSort(jsonArray)
            }
            response.code() != 200 -> {
                Log.i("getFile", "cookie失效")
                return null
            }
        }
        return null
    }

    private fun mSort(data: JSONArray): MutableList<JSONObject> {
        val list = mutableListOf<JSONObject>()
        for (i in 0 until data.length()) {
            list.add(data[i] as JSONObject)
        }
        return (list.filter { it.getString("itemType") == "DIR" }
            .sortedBy { it.getString("itemName") }
            .plus(list.filter { it.getString("itemType") == "FILE" }
                .sortedBy { it.getString("itemName") })).toMutableList()
    }

    private fun <T> sendRequest(call: Call<T>): Response<T>? {
        var response: Response<T>? = null
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
        return response
    }
}