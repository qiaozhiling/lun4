package com.qzl.cloudalbum.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.internet.MyUserImage
import com.qzl.cloudalbum.internet.NetHelper
import com.qzl.cloudalbum.other.*
import kotlinx.android.synthetic.main.activity_user_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Exception
import java.net.ConnectException

class UserDetailActivity : BaseActivity() {

    private var userImage: MyUserImage = MyUserImage(false, "", "", "", "", "", "")
    private var verify = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        val sPf = getSharedPreferences("login_setting", Context.MODE_PRIVATE)

        showHidden_Cb.isChecked = UserHelper.getShowHidden()

        refresh()

        //点击查看头像
        headPic_Iv.setOnClickListener {
            if (userImage.setHeaded) {
                val intent = Intent(this@UserDetailActivity, PicActivity::class.java)
                intent.putExtra("picUrl", "http://39.104.71.38:8080${userImage.fileX512URL}")
                startActivity(intent)
            }
        }

        //长按上传头像
        headPic_Iv.setOnLongClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            intent.putExtra("upType", 2)
            "请选择正方形图片".showToast(this)
            startActivityForResult(intent, 1)
            return@setOnLongClickListener false
        }

        //验证邮箱
        user_email_Tv.setOnClickListener {
            lifecycleScope.launch {
                try {
                    if (!verify) {
                        if (NetHelper.verifyEmail(null, this@UserDetailActivity)) {
                            "验证码以发送至邮箱${UserHelper.getEmail()}".showToastOnUi(this@UserDetailActivity)
                            val myDialog: AlertDialog.Builder =
                                AlertDialog.Builder(this@UserDetailActivity)
                            val edit = EditText(this@UserDetailActivity)
                            myDialog.setTitle("请输入验证码").setView(edit)

                            myDialog.setPositiveButton("确定") { _, _ ->
                                try {
                                    lifecycleScope.launch {
                                        val code = edit.text.toString()
                                        if (NetHelper.verifyEmail(code, this@UserDetailActivity)) {
                                            "邮箱已验证".showToastOnUi(this@UserDetailActivity)
                                            refresh()
                                        } else {
                                            "验证失败".showToastOnUi(this@UserDetailActivity)
                                        }
                                    }

                                } catch (e: ConnectException) {
                                    e.printStackTrace()
                                    //无网络提示
                                    "无网络的样子".showToast(this@UserDetailActivity)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    //测试提示
                                    "其他异常".showToast(this@UserDetailActivity)
                                }

                            }
                            myDialog.show()
                        }


                    } else "邮箱已验证".showToastOnUi(this@UserDetailActivity)
                } catch (e: ConnectException) {
                    e.printStackTrace()
                    //无网络提示
                    netErr(this@UserDetailActivity)
                } catch (e: Exception) {
                    e.printStackTrace()
                    //测试提示
                    "其他异常".showToastOnUi(this@UserDetailActivity)
                }

            }

        }

        back_Bt.setOnClickListener {
            finish()
        }

        //注销
        sign_out_Bt.setOnClickListener {
            sPf.edit().clear().apply()
            UserHelper.setShowHidden(false)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            ActivityCollector.finishAll()
        }

        //显示 隐藏文件
        showHidden_Cb.setOnCheckedChangeListener { _, isChecked ->
            UserHelper.setShowHidden(isChecked)
            sPf.edit().putBoolean("showHidden", isChecked).apply()
        }

        re_myinfo.setOnClickListener {
            "刷新".showToast(this)
            refresh()
        }

        resetPassword_RL.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        //重命名用户名
        userRename.setOnClickListener {
            AppDialog(this).setmTitle("请输入新名字").apply {
                setPositiveButton {
                    val newName = getText()
                    if (newName.length in 1..16 && UserHelper.nameInLaw(newName)) {
                        lifecycleScope.launch {

                            val string = NetHelper.reUserName(newName, this@UserDetailActivity)
                            string.showToastOnUi(this@UserDetailActivity)
                            dismiss()
                            refresh()


                        }
                    } else {
                        "用户名不合法".showToast(this@UserDetailActivity)
                    }
                }
                setNegativeButton {
                    "取消修改".showToast(this@UserDetailActivity)
                    dismiss()
                }
                show()
            }
        }
    }

    //刷新
    private fun refresh() {
        lifecycleScope.launch {

            //id email
            launch {
                try {

                    val user = NetHelper.getUserBaseInfo(this@UserDetailActivity)
                    user_name_Tv.text = user.name
                    user_email_Tv.text = user.emailAddress
                    Log.i("UserDetailActivity", user.name)
                    Log.i("UserDetailActivity", user.emailAddress)

                } catch (e: ConnectException) {
                    e.printStackTrace()
                    netErr(this@UserDetailActivity)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                    "其他异常".showToastOnUi(this@UserDetailActivity)
                }
            }

            //内存 验证信息
            launch {
                try {
                    val userInformation = NetHelper.getUserInfo(this@UserDetailActivity)
                    verify = userInformation.verify
                    val usedSize = userInformation.formatUsedSize
                    val totalSize = userInformation.formatTotalSize
                    info_contain_Tv.text =
                        "${usedSize}/${totalSize}"

                    if (verify) {
                        email_Tv.setTextColor(Color.GRAY)
                        user_email_Tv.setTextColor(Color.GRAY)
                    } else {
                        email_Tv.setTextColor(Color.RED)
                        user_email_Tv.setTextColor(Color.RED)
                    }

                } catch (e: ConnectException) {
                    e.printStackTrace()
                    netErr(this@UserDetailActivity)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                    "其他异常".showToastOnUi(this@UserDetailActivity)
                }

            }

            //加载头像
            launch(Dispatchers.IO) {
                try {
                    userImage = NetHelper.getHeadPic(this@UserDetailActivity)

                    if (userImage.setHeaded) {
                        val picUrl256 = "http://39.104.71.38:8080" + userImage.fileX256URL
                        /*  val picUrl512 = "http://39.104.71.38:8080" + userImage.fileX512URL*/
                        Log.i("headPic", picUrl256)

                        val requestOptions = RequestOptions()
                            .placeholder(R.mipmap.headpic)
                            .error(R.mipmap.headpicfail)

                        val header =
                            LazyHeaders.Builder().addHeader("cookie", UserHelper.getCookie())
                                .build()
                        val url = GlideUrl(picUrl256, header)

                        /* val file = Glide.with(this@UserDetailActivity).downloadOnly().load(url)
                             .submit().get()
                         file.delete()*/

                        withContext(Dispatchers.Main) {
                            Glide.with(this@UserDetailActivity).load(url).apply(requestOptions)
                                .into(headPic_Iv)

                        }

                        /* Glide.with(this@UserDetailActivity)
                             .downloadOnly()
                             .load(GlideUrl(picUrl512, header))
                             .submit().get()*/
                    }

                } catch (e: ConnectException) {
                    e.printStackTrace()
                    netErr(this@UserDetailActivity)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                    "其他异常".showToastOnUi(this@UserDetailActivity)
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)


        //??
        when {
            requestCode == 1 && resultCode == Activity.RESULT_OK -> {

                refresh()
            }
        }
    }
}