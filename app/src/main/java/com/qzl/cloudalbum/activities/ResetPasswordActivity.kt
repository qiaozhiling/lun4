package com.qzl.cloudalbum.activities

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.internet.NetHelper
import com.qzl.cloudalbum.other.UserHelper
import com.qzl.cloudalbum.other.netErr
import com.qzl.cloudalbum.other.showToast
import com.qzl.cloudalbum.other.showToastOnUi
import kotlinx.android.synthetic.main.activity_reset_password.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException

class ResetPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        rsp_cancel_button.setOnClickListener {
            finish()
        }

        rsp_commit_button.setOnClickListener {
            val email = rsp_et_useremail.text.toString()
            val old = rsp_et_oldpw.text.toString()
            val new = rsp_et_newpassword.text.toString()
            val renew = rsp_et_renewpassword.text.toString()

            when {
                email != UserHelper.getEmail() -> "email错误".showToast(this)
                new == "" || renew == "" -> "请输入密码".showToast(this)
                new.length !in 6..15 -> "请输入6-15字符密码".showToast(this)
                new != renew
                -> "请确认密码".showToast(this)
                else -> {
                    lifecycleScope.launch {
                        try {
                            if (NetHelper.resetPW(old, new, this@ResetPasswordActivity)) {
                                "修改成功".showToastOnUi(this@ResetPasswordActivity)
                                val intent =
                                    Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                                startActivity(intent)
                                ActivityCollector.finishAll()
                            } else {
                                "修改失败，反正看不见".showToastOnUi(this@ResetPasswordActivity)
                            }
                        } catch (e: ConnectException) {
                            e.printStackTrace()
                            netErr(this@ResetPasswordActivity)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            "其他异常".showToastOnUi(this@ResetPasswordActivity)
                        }
                    }

                }
            }
        }

    }
}