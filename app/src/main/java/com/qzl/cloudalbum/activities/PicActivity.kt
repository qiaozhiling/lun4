package com.qzl.cloudalbum.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.RequestOptions
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.other.UserHelper
import kotlinx.android.synthetic.main.activity_pic.*

class PicActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pic)
        supportActionBar?.hide()

        /*
        * image/gif
        * image/png
        * image/jpeg
        */

        val url = intent.getStringExtra("picUrl")

        url?.let {
            val picUrl = if (url.startsWith("http")) {
                val header =
                    LazyHeaders.Builder().addHeader("cookie", UserHelper.getCookie()).build()
                Header(GlideUrl(it, header))
            } else {
                Header(it)
            }


            val requestOptions: RequestOptions = RequestOptions()
                .fitCenter()
                .placeholder(R.mipmap.picloading)//加载中的占位符
                .error(R.mipmap.piclodingfail)//加载失败显示图片

            Glide.with(this)
                .load(picUrl.data)
                .apply(requestOptions)
                .thumbnail(0.2f)
                .into(picimage)
        }


        pic_activity.setOnClickListener {
            finish()
        }
    }

    inner class Header<T>(val data: T)
}