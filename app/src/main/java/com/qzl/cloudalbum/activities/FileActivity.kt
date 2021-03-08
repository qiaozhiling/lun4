package com.qzl.cloudalbum.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.adapter.FilesAdapter
import com.qzl.cloudalbum.internet.MyItem
import com.qzl.cloudalbum.internet.NetHelper
import com.qzl.cloudalbum.other.*
import kotlinx.android.synthetic.main.activity_file.*
import kotlinx.android.synthetic.main.title_layout.*
import kotlinx.android.synthetic.main.title_layout.view.*
import kotlinx.android.synthetic.main.toolbox_layout.*
import kotlinx.android.synthetic.main.tooltitle_layout.*
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.ConnectException

class FileActivity : BaseActivity() {
    private lateinit var thisPath: String//    "/root"   这一级的路径
    private lateinit var thisName: String//    "root"    这一级的名字

    private var filesAdapter: FilesAdapter? = null//adapter
    private var thisItem: MyItem? = null //这一级文件数据
    private var subItemsList: List<MyItem>? = null//子文件级列表


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file)
        supportActionBar?.hide()

        val sPf: SharedPreferences = getSharedPreferences("login_setting", Context.MODE_PRIVATE)
        //危 获取上Activity传来的信息
        thisPath = intent.getStringExtra("thisPath")!!//这一级的路径
        thisName = intent.getStringExtra("thisName")!!//这一级的名字

        setMyPopMenu(this, mtitle.titleMeun, cancel_search_btn)//设置标题栏菜单

        //recyclerView设置
        rv_files.layoutManager = LinearLayoutManager(this)//recyclerView layoutManager设置

        lifecycleScope.launch {
            //判断cookie是否过期
            try {
                if (!NetHelper.cookieInDate(this@FileActivity)) {//cookie过期 返回登入界面
                    sPf.edit().clear().apply()//清除spf数据
                    startActivity(Intent(this@FileActivity, LoginActivity::class.java))
                    "登入信息过期".showToastOnUi(this@FileActivity)
                    ActivityCollector.finishAll()
                } else {//cookie可用 加载文件信息
                    mtitle.titleText.text = thisName//标题为这一级的名字
                    //刷新列表
                    refreshFileList()
                    //设置点击事件
                    filesAdapter?.let {
                        setItemsClick()
                    }
                }
            } catch (e: ConnectException) {
                e.printStackTrace()
                netErr(this@FileActivity)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
                "其他异常".showToastOnUi(this@FileActivity)
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////

        //取消
        tool_cancel.setOnClickListener {
            toolShow(false)
        }

        //全选
        tool_select_all.setOnClickListener {
            filesAdapter?.let {

                if (it.getCheckedItems().size == getSubItemsList().size) {
                    it.setSubItemListCheck(false)//设置全不选
                    count.text = String.format(getString(R.string.checkNumber), 0)
                } else {
                    it.setSubItemListCheck(true)//设置全选
                    count.text =
                        String.format(getString(R.string.checkNumber), it.getCheckedItems().size)

                }
                it.notifyDataSetChanged()
            }
        }

        //新建文件夹
        newbuild.setOnClickListener {
            AppDialog(this).setmTitle("请输入文件夹名").showCheckBox(true).apply {
                setPositiveButton {
                    val name = getText()
                    if (!UserHelper.nameInLaw(name)) {
                        "文件名不合法".showToast(this@FileActivity)
                    } else {
                        lifecycleScope.launch {
                            try {

                                if (NetHelper.newBuildDir(
                                        "$thisPath/$name",
                                        getCheckState(),
                                        this@FileActivity
                                    )
                                ) {//创建成功
                                    "创建成功".showToastOnUi(this@FileActivity)//提示
                                    refreshFileList()//刷新列表
                                } else {
                                    "创建失败".showToastOnUi(this@FileActivity)
                                }
                            } catch (e: ConnectException) {
                                e.printStackTrace()
                                netErr(this@FileActivity)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                "其他异常".showToastOnUi(this@FileActivity)
                            } finally {
                                dismiss()
                            }
                        }
                    }
                }
                setNegativeButton {
                    "取消创建".showToast(this@FileActivity)
                    dismiss()
                }
                show()
            }

        }

        //上传图片
        upload.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            intent.putExtra("upType", 1)
            intent.putExtra("thisPath", thisPath)
            startActivity(intent)
        }

        //查看下载列表
        downloadlist.setOnClickListener {
            val intent = Intent(this, DownloadedActivity::class.java)
            startActivity(intent)
        }

        //删除选中
        delete_toolbox.setOnClickListener {
            lifecycleScope.launch {
                try {
                    filesAdapter?.delete()
                    toolShow(false)//隐藏工具栏
                    refreshFileList()
                } catch (e: ConnectException) {
                    e.printStackTrace()
                    netErr(this@FileActivity)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                    "其他异常".showToastOnUi(this@FileActivity)
                }
            }
        }

        //重命名
        rename_toolbox.setOnClickListener {
            AppDialog(this).setmTitle("请输入新文件名").apply {
                setPositiveButton {
                    val newName = getText()
                    if (!UserHelper.nameInLaw(newName)) {
                        "文件名不合法".showToast(this@FileActivity)
                    } else {
                        lifecycleScope.launch {
                            try {
                                if (filesAdapter!!.rename(newName)) {
                                    refreshFileList()
                                    "重命名成功".showToastOnUi(this@FileActivity)
                                    toolShow(false)
                                } else "重命名失败".showToastOnUi(this@FileActivity)
                            } catch (e: ConnectException) {
                                e.printStackTrace()
                                netErr(this@FileActivity)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                "其他异常".showToastOnUi(this@FileActivity)
                            } finally {
                                dismiss()
                            }
                        }
                    }
                }
                setNegativeButton {
                    "取消重命名".showToast(this@FileActivity)
                    dismiss()
                }
                show()
            }
        }

        //改变隐藏状态
        change_hide_status_toolbox.setOnClickListener {
            lifecycleScope.launch {
                try {
                    filesAdapter?.changeFileState()
                    refreshFileList()
                } catch (e: ConnectException) {
                    e.printStackTrace()
                    netErr(this@FileActivity)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                    "其他异常".showToastOnUi(this@FileActivity)
                }
            }
        }

        //下载
        download_toolbox.setOnClickListener {
            lifecycleScope.launch {
                toolShow(false)
                filesAdapter?.download()
            }
        }

        //分享
        share_toolbox.setOnClickListener {
            lifecycleScope.launch {
                toolShow(false)
                filesAdapter?.shareFile()
            }
        }

        //取消搜索
        cancel_search_btn.setOnClickListener {
            filesAdapter?.apply {
                reSetItemList(subItemsList!!)
                notifyDataSetChanged()
            }
            cancel_search_btn.visibility = View.GONE
        }

        //下拉刷新
        swipere_file.apply {
            setColorSchemeResources(R.color.black, R.color.AlbumBlue, R.color.AlbumBlue2)

            setOnRefreshListener {
                lifecycleScope.launch {
                    try {
                        refreshFileList()
                    } catch (e: ConnectException) {
                        e.printStackTrace()
                        netErr(this@FileActivity)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "其他异常".showToastOnUi(this@FileActivity)
                    }
                }
            }
        }

    }

    //返回页面刷新列表
    override fun onRestart() {
        super.onRestart()
        cancel_search_btn.visibility = View.GONE
        lifecycleScope.launch {
            try {
                refreshFileList()
            } catch (e: ConnectException) {
                e.printStackTrace()
                netErr(this@FileActivity)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
                "其他异常".showToastOnUi(this@FileActivity)
            }

        }
    }

    override fun onBackPressed() {
        if (filesAdapter?.isShowed == true)
            toolShow(false)
        else this.finish()
    }


    /* override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        *//* //设置菜单
        setMyPopMenu(this, mtitle.titleMeun)//设置标题栏菜单*//*
        return super.onCreateView(name, context, attrs)
    }*/


    ///////////////////////////////////////////////////////////////////////////////////////////////

    //刷新Item列表
    @Throws(Exception::class)
    private suspend fun refreshFileList() {
        try {
            //转圈显示
            swipere_file.isRefreshing = true

            cancel_search_btn.visibility = View.GONE

            //获取改目录数据
            thisItem = NetHelper.getFile(thisPath, this@FileActivity)//获取这一级的数据
            subItemsList = mSort(thisItem!!.subItems)//排序 子文件

            if (filesAdapter == null) {
                //adapter未实例 设置adapter
                setAdapter(getSubItemsList())
                //设置点击事件
                setItemsClick()
            } else {
                //adapter已有 更新数据
                filesAdapter!!.reSetItemList(subItemsList!!)
                filesAdapter!!.notifyDataSetChanged()
            }

        } catch (e: Exception) {
            throw e
        }finally {
            swipere_file.isRefreshing = false
        }
    }

    //设置adapter
    private fun setAdapter(sub: List<MyItem>) {
        filesAdapter = FilesAdapter(sub, thisPath, this)
        rv_files.adapter = filesAdapter
    }

    //设置Item点击事件
    private fun setItemsClick() {
        filesAdapter!!.setOnItemListener(object : FilesAdapter.OnItemClickListener {

            /*单击Item事件
            * position Item位置
            * subItemPath 子Item路径
            * subItem 子Item
            * */
            override fun setOnItemClick(position: Int, subItemPath: String, subItem: MyItem?) {
                if (filesAdapter!!.isShowed) {
                    filesAdapter!!.apply {
                        setItemChecked(position)
                        notifyItemChanged(position)
                    }
                } else {
                    if (subItem?.itemType == "DIR") {//是文件夹 跳转下一目录
                        val subItemName = subItem.itemName
                        val intent = Intent(this@FileActivity, FileActivity::class.java)
                        intent.putExtra("thisPath", subItemPath)
                        intent.putExtra("thisName", subItemName)
                        this@FileActivity.startActivity(intent)
                    } else if (subItem?.itemType == "FILE") {//图片 跳转查看
                        Log.i("Onclick", "Not DIR")
                        val picUrl = "http://39.104.71.38:8080${subItem.file?.fileURL}"//图片url
                        val intent = Intent(this@FileActivity, PicActivity::class.java)
                        intent.putExtra("picUrl", picUrl)
                        this@FileActivity.startActivity(intent)
                    }
                }

            }

            /*长按事件
            * position Item位置
            * */
            override fun setOnItemLongClick(position: Int): Boolean {
                toolShow(true, position)//显示工具栏
                return false
            }

            /*选框选中事件
            * position Item位置
            * isCheck 是否被选中
            * */
            override fun setOnItemCheckedChanged(position: Int, isCheck: Boolean) {
                if (!filesAdapter!!.canChecked) {
                    return
                }
                filesAdapter?.let {
                    it.setItemChecked(position)//设置选中
                    val check = it.getCheckedItems()
                    val num = check.size
                    //标题栏内容改变
                    count.text = String.format(getString(R.string.checkNumber), num)
                    Log.i("setOnItemCheckedChanged", "点击选框")
                    //选择不为过1个 重命名按钮消失
                    if (num != 1) rename_toolbox.visibility = View.GONE
                    else rename_toolbox.visibility = View.VISIBLE
                }
            }
        })
    }

    //设置菜单
    private fun setMyPopMenu(context: Context, view: View/*设置菜单的View*/, b: ImageButton) {
        val popMenu = PopupMenu(context, view)//菜单
        popMenu.menuInflater.inflate(R.menu.file_title_menu, popMenu.menu)//填充

        view.setOnClickListener {
            popMenu.show()
        }//设置点击标题栏菜单图标 显示菜单

        popMenu.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.userIf_item -> {//跳转个人信息界面
                    val intent = Intent(this, UserDetailActivity::class.java)
                    startActivity(intent)
                }

                R.id.search_item -> {//搜索
                    AppDialog(this).setmTitle("请输入文件夹名").apply {
                        setPositiveButton {
                            val target = getText()
                            if (target != "") {
                                filesAdapter?.reSetItemList(subItemsList?.filter { item ->
                                    item.itemName.contains(target)
                                }!!)
                                filesAdapter?.notifyDataSetChanged()

                                b.visibility = View.VISIBLE


                            } else {
                                "请输入".showToast(this@FileActivity)
                            }
                            dismiss()
                        }
                        setNegativeButton {
                            "取消搜索".showToast(this@FileActivity)
                            dismiss()
                        }
                        show()
                    }
                }

                R.id.get_share_item -> {
                    AppDialog(this).setmTitle("请输入分享码").apply {
                        setPositiveButton {
                            val shareCode = getText()
                            if (shareCode == "") {
                                "请输入分享码".showToast(this@FileActivity)
                            } else {
                                lifecycleScope.launch {
                                    try {
                                        if (NetHelper.getShare(
                                                shareCode,
                                                thisPath,
                                                this@FileActivity
                                            )
                                        ) {
                                            "已保存至当前目录下".showToastOnUi(this@FileActivity)
                                            refreshFileList()
                                        }
                                    } catch (e: ConnectException) {
                                        e.printStackTrace()
                                        netErr(this@FileActivity)
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        "其他异常".showToastOnUi(this@FileActivity)
                                    }
                                }
                            }
                            dismiss()
                        }
                        setNegativeButton {
                            "取消".showToast(this@FileActivity)
                            dismiss()
                        }
                        show()
                    }
                }
            }//菜单内部点击事件
            return@setOnMenuItemClickListener true
        }
    }

    //排序列表
    private fun mSort(list: List<MyItem>): List<MyItem> =
        list.filter { it.itemType == "DIR" }.sortedBy { it.itemName }
            .plus(list.filter { it.itemType == "FILE" }.sortedBy { it.itemName })


    //工具栏显示
    private fun toolShow(show: Boolean, position: Int = -1) {
        filesAdapter?.let {
            if (show) {
                //设置工具栏显示
                mtitle.visibility = View.GONE
                mtooltitle.visibility = View.VISIBLE
                mtoolbox.visibility = View.VISIBLE
                //设置CheckBox显示
                it.isShowed = show//显示Checkbox

                it.setSubItemListCheck(false)  //选中状态归零

                it.setItemChecked(position)//长按的选中

                count.text = String.format(getString(R.string.checkNumber), 1)
                rename_toolbox.visibility = View.VISIBLE
                it.notifyDataSetChanged()
            } else {
                //消失
                mtitle.visibility = View.VISIBLE
                mtooltitle.visibility = View.GONE
                mtoolbox.visibility = View.GONE

                it.isShowed = show//隐藏Checkbox
                it.canChecked = false
                it.notifyDataSetChanged()
            }
        }
    }

    //？？？？？？？？？待定
    private fun getSubItemsList(): List<MyItem> {
        return subItemsList!!
    }


}

