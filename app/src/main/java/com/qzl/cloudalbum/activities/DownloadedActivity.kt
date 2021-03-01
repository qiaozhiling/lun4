package com.qzl.cloudalbum.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.adapter.DLPicAdapter
import com.qzl.cloudalbum.database.AppDatabase
import com.qzl.cloudalbum.database.DownloadPic
import kotlinx.android.synthetic.main.activity_downloaded.*
import kotlinx.android.synthetic.main.activity_file.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

class DownloadedActivity : AppCompatActivity() {
    private var list: List<DownloadPic>? = null
    private lateinit var dLPAdapter: DLPicAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_downloaded)

        val dLPDao = AppDatabase.getDatabase(this).getDLPicDao()

        dl_rc.layoutManager = LinearLayoutManager(this)//recyclerView layoutManager设置

        lifecycleScope.launch {

            list = dLPDao.loadAllDLPics()
            dLPAdapter = DLPicAdapter(list!!, this@DownloadedActivity)
            dl_rc.adapter = dLPAdapter
            dLPAdapter.setOnItemListener(object : DLPicAdapter.OnItemClickListener {
                override fun setOnItemLongClick(position: Int): Boolean {
                    TODO("Not yet implemented")
                }

                override fun setOnItemClick(position: Int, pic: DownloadPic?) {
                    pic?.let {
                        val picUrl = it.localPath//图片url
                        val intent = Intent(this@DownloadedActivity, PicActivity::class.java)
                        intent.putExtra("picUrl", picUrl)
                        this@DownloadedActivity.startActivity(intent)
                    }

                }

            })
        }

    }
}