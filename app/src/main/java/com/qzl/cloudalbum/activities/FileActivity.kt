package com.qzl.cloudalbum.activities

import android.os.Bundle
import android.widget.Toast
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.internet.LRData
import com.qzl.cloudalbum.other.MyHelper
import kotlinx.android.synthetic.main.title_layout.*

class FileActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file)

        supportActionBar?.hide()
        //设置菜单
        MyHelper.setMyPopMenu(this, titleMeun)

        val rDate = intent.getSerializableExtra("data") as LRData

        Toast.makeText(this, rDate.date, Toast.LENGTH_SHORT).show()
    }

}