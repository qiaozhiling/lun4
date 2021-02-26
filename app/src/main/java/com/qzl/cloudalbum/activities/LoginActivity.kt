package com.qzl.cloudalbum.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.internet.CldAbService
import com.qzl.cloudalbum.internet.ServiceCreator
import com.qzl.cloudalbum.other.UserHelper
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    /*
        login_setting(sDF):
            id:String
            password:String
            cookie:String
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        val sPf = getSharedPreferences("login_setting", Context.MODE_PRIVATE)

        sPf.let {
            if (it.getBoolean("isLogged", false)) {
                UserHelper.setCookie(it.getString("Cookie", null))
                UserHelper.setId(it.getString("id", null))
                UserHelper.setPassword(it.getString("password", null))
                UserHelper.setShowHidden(it.getBoolean("showHidden", false))

                /*Log.i("Helper", UserHelper.getCookie() + "")
                Log.i("Helper", UserHelper.getShowHidden().toString())
                Log.i("Helper", UserHelper.getId() + "")
                Log.i("Helper", UserHelper.getPassword() + "")*/
                toFile()
            }
        }

        //跳转注册
        tv_reg.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        //忘记密码
        tv_fgPw.setOnClickListener {
            Toast.makeText(this, "请联系管理员", Toast.LENGTH_SHORT).show()
        }

        btn_login.setOnClickListener {

            if (et_username.text.toString() == "" || et_password.text.toString() == "") {
                //账号或密码为空
                Toast.makeText(this, "请输入账号密码", Toast.LENGTH_SHORT).show()
            } else {

                val uid = et_username.text.toString()
                val paswd = et_password.text.toString()

                Thread {
                    UserHelper.login(uid, paswd, sPf).let {
                        when {
                            it == true -> {
                                toFile()
                            }
                            it != true -> {
                                Looper.prepare()
                                Toast.makeText(this@LoginActivity, "登入失败", Toast.LENGTH_SHORT)
                                    .show()
                                Looper.loop()
                            }
                        }
                    }
                }.start()

            }
        }

    }//登入获取cookie

    private fun toFile() {
        val intent = Intent(this, FileActivity::class.java)
        //  /root
        intent.putExtra("thisPath", "/root")
        intent.putExtra("thisName", "root")
        this.startActivity(intent)
        this.finish()
    }

}
/*    private fun getFile() {
        var response: Response<ResponseBody>? = null
        val t = Thread {
            try {
                response = service.getFileItem(UserHelper.getCookie(), "/root").clone().execute()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        t.start()
        t.join()

        if (response == null) {
            Log.i("getFile", "网络链接失败")
        } else if (response!!.code() == 200) {

            val json = response!!.body()!!.string()
            val data = JSONObject(json).getJSONObject("data")
            subItemJson = data.getJSONArray("subItems").toString()
        } else if (response!!.code() != 200) {
            Log.i("getFile", "cookie失效")
        }

    }
}*/


/*private fun getFileData(service: CldAbService) {

    service.getFileItem(Helper.getCookie()).enqueue(object : Callback<ResponseBody> {
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            t.printStackTrace()
            Toast.makeText(this@LoginActivity, "getFileData File", Toast.LENGTH_SHORT).show()
        }

        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

            if (response.code() == 200) {
                response.body()?.let {

                    //存cookie
                    val editor = sPf.edit()
                    editor.putString("Set-Cookie", Helper.getCookie())
                    editor.apply()

                    //跳转
                    val parentPath = ""
                    val thisPath = "/root"
                    val intent = Intent(this@LoginActivity, FileActivity::class.java)
                    intent.putExtra("parentPath", parentPath)
                    intent.putExtra("thisPath", thisPath)
                    startActivity(intent)

                    //关闭页面
                    this@LoginActivity.finish()

                }

                Log.i("xc getFile", "response.body==null")

            } else {
                Log.i("xc", "账号cookie过期或丢失")
            }

        }

    })
}*/