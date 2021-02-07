package com.qzl.cloudalbum.activities

import android.content.Intent
import android.os.Bundle
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.other.MyHelper
import kotlinx.android.synthetic.main.activity_file.*
import kotlinx.android.synthetic.main.title_layout.*

class FileActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file)

        //设置菜单
        MyHelper.setMyPopMenu(this, titleMeun)

    }

}