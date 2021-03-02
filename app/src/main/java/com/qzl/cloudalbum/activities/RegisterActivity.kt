package com.qzl.cloudalbum.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.internet.CldAbService
import com.qzl.cloudalbum.internet.ServiceCreator
import com.qzl.cloudalbum.other.UserHelper
import com.qzl.cloudalbum.other.netErr
import com.qzl.cloudalbum.other.showToast
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.net.ConnectException

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

            lifecycleScope.launch {
                try {

                    when {
                        name == "" ->
                            "用户名为空".showToast(this@RegisterActivity)
                        password != repassword || password == "" ->
                            "密码错误".showToast(this@RegisterActivity)
                        !UserHelper.emailInLaw(email) ->
                            "邮箱格式错误或已被注册".showToast(this@RegisterActivity)
                        else -> {
                            val success = UserHelper.register(email, name, password)
                            this@RegisterActivity.let {
                                if (success) {
                                    "创建成功".showToast(it)
                                    it.finish()
                                } else "创建失败".showToast(it)
                            }
                        }
                    }
                } catch (e: ConnectException) {
                    e.printStackTrace()
                    netErr(this@RegisterActivity)
                } catch (e: Exception) {
                    e.printStackTrace()
                    "其他异常".showToast(this@RegisterActivity)
                }
            }
        }
    }
}