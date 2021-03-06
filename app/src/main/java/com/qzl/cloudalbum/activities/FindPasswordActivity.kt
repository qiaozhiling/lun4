package com.qzl.cloudalbum.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.internet.NetHelper
import com.qzl.cloudalbum.other.netErr
import com.qzl.cloudalbum.other.showToast
import com.qzl.cloudalbum.other.showToastOnUi
import kotlinx.android.synthetic.main.activity_find_password.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException

class FindPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_find_password)

        rsp_button_cancel.setOnClickListener {
            finish()
        }

        get.setOnClickListener {
            val email = rsp_et_username.text.toString()
            if (email != "") {
                lifecycleScope.launch {
                    try {
                        if (NetHelper.findPasword(email, this@FindPasswordActivity)) {
                            "验证码已发送至邮箱\n$email".showToastOnUi(this@FindPasswordActivity)
                        } else {
                            "发送失败 ，多半看不见这条".showToastOnUi(this@FindPasswordActivity)
                        }
                    } catch (e: ConnectException) {
                        e.printStackTrace()
                        netErr(this@FindPasswordActivity)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "其他异常".showToastOnUi(this@FindPasswordActivity)
                    }

                }
            } else {
                "请输入email".showToast(this)
            }

        }

        rsp_button.setOnClickListener {
            val email = rsp_et_username.text.toString()
            val code = rsp_et_email.text.toString()
            val pw = rsp_et_password.text.toString()
            val repw = rsp_et_repassword.text.toString()

            when {
                (email == "") -> "请输入email".showToast(this)
                code == "" -> "请输入验证码".showToast(this)
                pw == "" || repw == "" -> "请输入密码".showToast(this)
                pw != repw -> "请确认密码".showToast(this)
                else -> lifecycleScope.launch {
                    try {
                        if (NetHelper.findPasword(email, code, pw, this@FindPasswordActivity)) {
                            "${email}\n找回密码成功".showToastOnUi(this@FindPasswordActivity)
                            this@FindPasswordActivity.finish()
                        } else {
                            "找回密码失败 ，多半也看不见这条".showToastOnUi(this@FindPasswordActivity)
                        }
                    } catch (e: ConnectException) {
                        e.printStackTrace()
                        netErr(this@FindPasswordActivity)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "其他异常".showToastOnUi(this@FindPasswordActivity)
                    }

                }
            }
        }

    }
}