package com.qzl.cloudalbum.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.internet.MyUserImage
import com.qzl.cloudalbum.other.UserHelper
import com.qzl.cloudalbum.other.netErr
import com.qzl.cloudalbum.other.showToast
import kotlinx.android.synthetic.main.activity_user_detail.*
import kotlinx.android.synthetic.main.dialog.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.net.ConnectException

class UserDetailActivity : BaseActivity() {

    private var userImage: MyUserImage = MyUserImage(false, "", "", "", "", "", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        val sPf = getSharedPreferences("login_setting", Context.MODE_PRIVATE)

        showHidden_Cb.isChecked = UserHelper.getShowHidden()
        lifecycleScope.launch {

            launch {
                try {

                    val user = UserHelper.getUserBaseInfo()
                    user_name_Tv.text = user.name
                    user_email_Tv.text = user.emailAddress

                } catch (e: ConnectException) {
                    e.printStackTrace()
                    //无网络提示
                    netErr(this@UserDetailActivity)
                } catch (e: Exception) {
                    e.printStackTrace()
                    //测试提示
                    "其他异常".showToast(this@UserDetailActivity)
                }
            }

            launch {
                try {

                    val userInformation = UserHelper.getUserInfo()
                    val verify = userInformation.verify
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
                    //无网络提示
                    netErr(this@UserDetailActivity)
                } catch (e: Exception) {
                    e.printStackTrace()
                    //测试提示
                    "其他异常".showToast(this@UserDetailActivity)
                }

            }

            launch {
                try {
                    userImage = UserHelper.getHeadPic()

                    if (userImage.setHeaded) {
                        val picUrl256 = "http://39.104.71.38:8080" + userImage.fileX256URL
                        val picUrl512 = "http://39.104.71.38:8080" + userImage.fileX512URL
                        Log.i("headPic", picUrl256)
                        val requestOptions = RequestOptions()
                            .placeholder(R.mipmap.headpic)
                            .error(R.mipmap.headpicfail)

                        val header =
                            LazyHeaders.Builder().addHeader("cookie", UserHelper.getCookie())
                                .build()

                        Glide.with(this@UserDetailActivity).load(GlideUrl(picUrl256, header))
                            .apply(requestOptions)
                            .into(headPic_Iv)

                        withContext(Dispatchers.IO) {
                            Glide.with(this@UserDetailActivity).downloadOnly()
                                .load(GlideUrl(picUrl512, header)).submit().get()
                        }
                    }

                } catch (e: ConnectException) {
                    e.printStackTrace()
                    //无网络提示
                    netErr(this@UserDetailActivity)
                } catch (e: Exception) {
                    e.printStackTrace()
                    //测试提示
                    "其他异常".showToast(this@UserDetailActivity)
                }
            }

        }

        headPic_Iv.setOnClickListener {
            if (userImage.setHeaded) {
                val intent = Intent(this@UserDetailActivity, PicActivity::class.java)
                intent.putExtra("picUrl", "http://39.104.71.38:8080" + userImage.fileX512URL)
                startActivity(intent)
            }
        }

        back_Bt.setOnClickListener {
            finish()
        }

        sign_out_Bt.setOnClickListener {
            sPf.edit().clear().apply()
            UserHelper.setShowHidden(false)
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            ActivityCollector.finishAll()
        }//注销

        showHidden_Cb.setOnCheckedChangeListener { buttonView, isChecked ->
            UserHelper.setShowHidden(isChecked)
            sPf.edit().putBoolean("showHidden", isChecked).apply()
        }//显示 隐藏文件


    }
}