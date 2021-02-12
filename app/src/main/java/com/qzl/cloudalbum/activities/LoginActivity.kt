package com.qzl.cloudalbum.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.internet.*
import com.qzl.cloudalbum.other.MyHelper
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        val sPf = getSharedPreferences("login_setting", Context.MODE_PRIVATE)
        remPw.isChecked = sPf.getBoolean("remPw", false)
        autoLi.isChecked = sPf.getBoolean("autoLi", false)

        if (remPw.isChecked) {
            et_username.setText(sPf.getString("uid", ""))
            et_password.setText(sPf.getString("paswd", ""))
        }

        btn_login.setOnClickListener {

            if (et_username.text.toString() == "" || et_password.text.toString() == "") {
                Toast.makeText(this, "请输入账号密码", Toast.LENGTH_SHORT).show()
            } else {

                val idPassword = IdPw(et_username.text.toString(), et_password.text.toString())
                val mServer = ServiceCreator.create(LoginService::class.java)

                mServer.loginPost(idPassword).enqueue(object : Callback<LRData> {
                    override fun onFailure(call: Call<LRData>, t: Throwable) {
                        Log.i("Login", "Connection Failed")
                    }

                    override fun onResponse(call: Call<LRData>, response: Response<LRData>) {
                        val rData = response.body()
                        Log.i("Login", "Connection Succeed")
                        if (rData == null) {
                            Log.i("Login", "rData==null")

                        } else {
                            if (rData.err) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    rData.message,
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {//登入成功
                                if (remPw.isChecked) {
                                    MyHelper.saveLSetting(
                                        idPassword,
                                        sPf,
                                        remPw.isChecked,
                                        autoLi.isChecked
                                    )

                                }
                                val intent =
                                    Intent(this@LoginActivity, FileActivity::class.java)
                                intent.putExtra("data", rData)
                                this@LoginActivity.startActivity(intent)
                                this@LoginActivity.finish()

                            }
                        }
                    }
                })
            }

        }

        remPw.setOnCheckedChangeListener { compoundButton: CompoundButton, isChecked: Boolean ->
            if (!remPw.isChecked) {
                val editor = getSharedPreferences("login_setting", Context.MODE_PRIVATE).edit()
                editor.clear()
                editor.apply()
            }
        }

        tv_reg.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        if (remPw.isChecked && autoLi.isChecked) {
            runOnUiThread {
                btn_login.performClick()
            }
        }
    }
}