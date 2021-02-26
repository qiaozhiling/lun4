package com.qzl.cloudalbum.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.internet.CldAbService
import com.qzl.cloudalbum.internet.ServiceCreator
import kotlinx.android.synthetic.main.activity_register.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        reg_button_cancel.setOnClickListener {
            finish()
        }

        reg_button.setOnClickListener {
            val name = reg_et_username.text.toString()
            val email = reg_et_email.text.toString()
            val password = reg_et_password.text.toString()
            val repassword = reg_et_repassword.text.toString()

            Log.i("password.length", (password.length in 6..15).toString())
            Log.i("repassword == password", (repassword == password).toString() + "")
            Log.i("name != \"\"", (name != "").toString())
            Log.i(
                "email",
                (email.matches(Regex("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(.[a-zA-Z0-9_-]+)+\$"))).toString()
            )

            if (password.length in 6..15 && repassword == password && name != "") {
                if (email.matches(Regex("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(.[a-zA-Z0-9_-]+)+\$"))) {

                    //输入格式正确 开始注册请求
                    val service = ServiceCreator.create(CldAbService::class.java)
                    service.register(email, password, name)
                        .enqueue(object : Callback<ResponseBody> {
                            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                t.printStackTrace()
                                Toast.makeText(this@RegisterActivity, "请检查网络", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            override fun onResponse(
                                call: Call<ResponseBody>,
                                response: Response<ResponseBody>
                            ) {
                                val json = response.body()?.string()
                                val err = JSONObject(json).getBoolean("err")

                                if (err) {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "注册失败-${JSONObject(json).getString("message")}",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "注册成功",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    this@RegisterActivity.finish()
                                }
                            }

                        })


                } else {
                    Toast.makeText(this, "请检查邮箱", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "请检查密码", Toast.LENGTH_SHORT).show()

            }
        }
    }
}