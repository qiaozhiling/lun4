package com.qzl.cloudalbum.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.adapter.DLPicAdapter
import com.qzl.cloudalbum.database.AppDatabase
import com.qzl.cloudalbum.database.DownloadPic
import com.qzl.cloudalbum.database.DownloadPicDao
import com.qzl.cloudalbum.other.UserHelper
import kotlinx.android.synthetic.main.activity_downloaded.*
import kotlinx.coroutines.launch
import java.io.File

class DownloadedActivity : AppCompatActivity() {
    private var list: List<DownloadPic>? = null
    private lateinit var dLPAdapter: DLPicAdapter
    private lateinit var dLPDao: DownloadPicDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_downloaded)

        dLPDao = AppDatabase.getDatabase(this).getDLPicDao()

        dl_rc.layoutManager = LinearLayoutManager(this)//recyclerView layoutManager设置


        lifecycleScope.launch {

            list = dLPDao.loadDLPics(UserHelper.getEmail())
            dLPAdapter = DLPicAdapter(list!!, this@DownloadedActivity)
            dl_rc.adapter = dLPAdapter
            dLPAdapter.setOnItemListener(object : DLPicAdapter.OnItemClickListener {
                override fun setOnItemClick(position: Int, pic: DownloadPic?) {
                    pic?.let {
                        //下载成功 可跳转
                        if (pic.success) {
                            val picUrl = it.localPath//图片url
                            val intent =
                                Intent(this@DownloadedActivity, PicActivity::class.java)
                            intent.putExtra("picUrl", picUrl)
                            this@DownloadedActivity.startActivity(intent)
                        }
                    }

                }

                override fun setOnItemLongClick(
                    position: Int,
                    pic: DownloadPic?,
                    itemView: View
                ): Boolean {

                    val popMenu = PopupMenu(this@DownloadedActivity, itemView)//菜单
                    popMenu.menuInflater.inflate(R.menu.file_download_menu, popMenu.menu)//填充

                    popMenu.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.delete_menu -> {
                                lifecycleScope.launch {
                                    dLPDao.dLPicDelete(pic!!)
                                    val file = File(pic.localPath)
                                    file.delete()
                                    dLPAdapter.picList =
                                        dLPDao.loadDLPics(UserHelper.getEmail())
                                    dLPAdapter.notifyDataSetChanged()
                                }

                            }//菜单内部点击事件
                        }
                        return@setOnMenuItemClickListener true
                    }
                    popMenu.show()
                    return false
                }


            })

        }

        swipere_dl.apply {
            setColorSchemeResources(R.color.black, R.color.AlbumBlue, R.color.AlbumBlue2)

            setOnRefreshListener {
                refresh()
                isRefreshing = false
            }
        }
    }

    private fun refresh() {
        lifecycleScope.launch {
            list = dLPDao.loadDLPics(UserHelper.getEmail())
            dLPAdapter.picList = list!!
            dLPAdapter.notifyDataSetChanged()
        }
    }

}

