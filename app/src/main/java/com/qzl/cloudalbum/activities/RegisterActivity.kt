package com.qzl.cloudalbum.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.internet.NetHelper
import com.qzl.cloudalbum.other.netErr
import com.qzl.cloudalbum.other.showToastOnUi
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.Exception
import java.net.ConnectException

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        rsp_cancel_button.setOnClickListener {
            finish()
        }

        rsp_commit_button.setOnClickListener {
            val name = rsp_et_useremail.text.toString()
            val email = rsp_et_oldpw.text.toString()
            val password = rsp_et_newpassword.text.toString()
            val repassword = rsp_et_renewpassword.text.toString()

            lifecycleScope.launch {
                try {

                    when {
                        name == "" ->
                            "用户名为空".showToastOnUi(this@RegisterActivity)
                        password != repassword || password == "" ->
                            "密码错误".showToastOnUi(this@RegisterActivity)
                        !NetHelper.emailInLaw(email, this@RegisterActivity) ->
                            "邮箱格式错误或已被注册".showToastOnUi(this@RegisterActivity)
                        else -> {
                            val success =
                                NetHelper.register(email, name, password, this@RegisterActivity)
                            this@RegisterActivity.let {
                                if (success) {
                                    "创建成功".showToastOnUi(it)
                                    it.finish()
                                } else "创建失败".showToastOnUi(it)
                            }
                        }
                    }
                } catch (e: ConnectException) {
                    e.printStackTrace()
                    netErr(this@RegisterActivity)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                    "其他异常".showToastOnUi(this@RegisterActivity)
                }
            }
        }
    }
}