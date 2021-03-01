package com.qzl.cloudalbum.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.adapter.FilesAdapter
import com.qzl.cloudalbum.internet.MyItem
import com.qzl.cloudalbum.other.UserHelper
import com.qzl.cloudalbum.other.netErr
import com.qzl.cloudalbum.other.showToast
import kotlinx.android.synthetic.main.activity_file.*
import kotlinx.android.synthetic.main.title_layout.view.*
import kotlinx.android.synthetic.main.toolbox_layout.*
import kotlinx.android.synthetic.main.tooltitle_layout.*
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.ConnectException

class FileActivity : BaseActivity() {
    private lateinit var thisPath: String//    /root//这一级的路径
    private lateinit var thisName: String//    root//这一级的名字

    private var subFileStorageList: List<MyItem>? = null//子文件级列表
    private var thisFileStorage: MyItem? = null //这一级的数据
    private lateinit var filesAdapter: FilesAdapter//adapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file)
        supportActionBar?.hide()
        //设置菜单
        val sPf: SharedPreferences = getSharedPreferences("login_setting", Context.MODE_PRIVATE)
        //危
        thisPath = intent.getStringExtra("thisPath")!!//这一级的路径
        thisName = intent.getStringExtra("thisName")!!//这一级的名字

        lifecycleScope.launch {
            //判断cookie是否过期
            try {
                if (!UserHelper.cookieInDate()) {//过期 返回登入界面
                    sPf.edit().clear().apply()
                    startActivity(Intent(this@FileActivity, LoginActivity::class.java))
                    ActivityCollector.finishAll()
                } else {

                    mtitle.titleText.text = thisName//标题为这一级的名字
                    Log.i("intentData", thisPath + "----" + thisName)
                    thisFileStorage = UserHelper.getFile(thisPath)//获取这一级的数据
                    subFileStorageList = mSort(thisFileStorage!!.subItems)//排序 子文件
                    setAdapter()
                }
                //未过期 无操作
            } catch (e: ConnectException) {
                e.printStackTrace()
                //无网络提示
                netErr(this@FileActivity)
            } catch (e: Exception) {
                e.printStackTrace()
                //测试提示
                "其他异常".showToast(this@FileActivity)
            }
        }

        setMyPopMenu(this, mtitle.titleMeun)//设置标题栏菜单
        //recyclerView设置
        rv_files.layoutManager = LinearLayoutManager(this)//recyclerView layoutManager设置

        tool_cancel.setOnClickListener {
            toolShow(false)
        }//取消

        tool_select_all.setOnClickListener {
            if (ckCount(filesAdapter.ischeck) == filesAdapter.ischeck.size) {
                filesAdapter.ischeck = MutableList(subFileStorageList!!.size) { i -> false }
                count.text = "已选择0个"
            } else {
                filesAdapter.ischeck = MutableList(subFileStorageList!!.size) { i -> true }
                count.text = "已选择${filesAdapter.ischeck.size}个"
            }
            filesAdapter.notifyDataSetChanged()
        }//全选

        newbuild.setOnClickListener {
            val view = LayoutInflater.from(this).inflate(R.layout.dialog, null)
            val myDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            myDialog.setTitle("请输入文件夹名").setView(view)

            myDialog.setPositiveButton("确定") { dialog, which ->
                val edit = view.findViewById(R.id.dir_name_Et) as EditText
                val checkBox = view.findViewById(R.id.create_hide_Cb) as CheckBox
                val name = edit.text.toString()

                if (!UserHelper.nameInLaw(name)) {
                    "文件名不合法".showToast(this)
                    return@setPositiveButton
                }

                lifecycleScope.launch {
                    try {
                        val succee = UserHelper.newBuildDir("$thisPath/$name", checkBox.isChecked)
                        if (succee) {//创建成功
                            "创建成功".showToast(this@FileActivity)//提示
                            refreshFileList()//刷新列表
                        } else {
                            "创建失败".showToast(this@FileActivity)
                        }
                    } catch (e: ConnectException) {
                        e.printStackTrace()
                        //无网络提示
                        netErr(this@FileActivity)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        //测试提示
                        "其他异常".showToast(this@FileActivity)
                    }
                }

            }

            myDialog.setNegativeButton("取消") { dialog, which ->
                "取消创建".showToast(this)
            }
            myDialog.show()
        }//新建文件夹

        upload.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            intent.putExtra("thisPath", thisPath)
            startActivityForResult(intent, 1)
        }//上传图片

        downloadlist.setOnClickListener {
            val intent = Intent(this, DownloadedActivity::class.java)
            startActivity(intent)
        }//查看下载列表

        delete_toolbox.setOnClickListener {

            toolShow(false)//隐藏工具栏
            lifecycleScope.launch {
                filesAdapter.delete()
                refreshFileList()
            }

            Log.i("delete", "delete")

        }//删除选中

        rename_toolbox.setOnClickListener {
            val myDialog: AlertDialog.Builder = AlertDialog.Builder(this)
            val editText = EditText(this)
            myDialog.setTitle("请输入新文件名").setView(editText)
            myDialog.setPositiveButton("确定") { dialog, which ->
                val newName = editText.text.toString()

                if (!UserHelper.nameInLaw(newName)) {
                    "文件名不合法".showToast(this)
                    return@setPositiveButton
                }

                lifecycleScope.launch {
                    try {
                        if (filesAdapter.rename(newName)) {
                            refreshFileList()
                            "成功".showToast(this@FileActivity)
                        } else "失败".showToast(this@FileActivity)
                    } catch (e: Exception) {
                        netErr(this@FileActivity)
                    }
                }

            }

            myDialog.setNegativeButton("取消") { dialog, which ->
                "取消".showToast(this)
            }
            myDialog.show()

        }//重命名

        change_hide_status_toolbox.setOnClickListener {
            lifecycleScope.launch {
                try {
                    filesAdapter.changeFileState()
                    refreshFileList()
                } catch (e: Exception) {
                    netErr(this@FileActivity)
                }
            }
        }//改变隐藏状态

        download_toolbox.setOnClickListener {
            toolShow(false)
            filesAdapter.download()
        }//下载

    }


    private fun setMyPopMenu(context: Context, view: View/*设置菜单的View*/) {
        val popMenu = PopupMenu(context, view)//菜单
        popMenu.menuInflater.inflate(R.menu.file_title_menu, popMenu.menu)//填充
        view.setOnClickListener {
            popMenu.show()
        }//设置点击标题栏菜单图标 显示菜单
        popMenu.setOnMenuItemClickListener {
            when (it.itemId) {

                R.id.userIf_item -> {//个人信息
                    Log.i("errrrrr", "-1")
                    val intent = Intent(this, UserDetailActivity::class.java)
                    startActivity(intent)
                }

                R.id.search_item -> {//搜索

                }

                R.id.refresh_item -> {//刷新
                    refreshFileList()
                }

                //test cookie失效
                R.id.testttttttt -> {
                    val sPf: SharedPreferences =
                        getSharedPreferences("login_setting", Context.MODE_PRIVATE)
                    sPf.edit().putString("Cookie", "afa").apply()
                    finish()
                }

                /*R.id.logOut_item -> {//注销账号
                    context.getSharedPreferences(
                        "login_setting",
                        AppCompatActivity.MODE_PRIVATE
                    )
                        .edit().clear().apply()
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    ActivityCollector.finishAll()
                }*/
            }//菜单内部点击事件
            return@setOnMenuItemClickListener true
        }
    }

    private fun toolShow(show: Boolean) {
        if (show) {
            //设置工具栏显示
            mtitle.visibility = View.GONE
            mtooltitle.visibility = View.VISIBLE
            mtoolbox.visibility = View.VISIBLE
            //设置CheckBox显示
            filesAdapter.isShowed = true
            filesAdapter.ischeck = MutableList(subFileStorageList!!.size) { i -> false }
            filesAdapter.notifyDataSetChanged()
        } else {
            mtitle.visibility = View.VISIBLE
            mtooltitle.visibility = View.GONE
            mtoolbox.visibility = View.GONE
            filesAdapter.isShowed = false
            filesAdapter.notifyDataSetChanged()
        }
    }

    private fun ckCount(l: MutableList<Boolean>): Int = l.count { it == true }

    private fun setAdapter() {

        val checklist = MutableList(subFileStorageList!!.size) { i -> false }
        filesAdapter = FilesAdapter(subFileStorageList!!, checklist, thisPath, this)
        rv_files.adapter = filesAdapter

        filesAdapter.setOnItemListener(object : FilesAdapter.OnItemClickListener {

            override fun setOnItemClick(position: Int, subItemPath: String, fileStorage: MyItem?) {

                if (fileStorage?.itemType == "DIR") {//是文件夹 跳转
                    val subItemName = fileStorage.itemName
                    val intent = Intent(this@FileActivity, FileActivity::class.java)
                    intent.putExtra("thisPath", subItemPath)
                    intent.putExtra("thisName", subItemName)
                    this@FileActivity.startActivity(intent)
                } else if (fileStorage?.itemType == "FILE") {//图片
                    Log.i("Onclick", "Not DIR")
                    val picUrl = "http://39.104.71.38:8080${fileStorage.file?.fileURL}"//图片url
                    val intent = Intent(this@FileActivity, PicActivity::class.java)
                    intent.putExtra("picUrl", picUrl)
                    this@FileActivity.startActivity(intent)
                }
            }

            override fun setOnItemLongClick(position: Int): Boolean {
                toolShow(true)
                filesAdapter.ischeck[position] = true
                return false
            }

            override fun setOnItemCheckedChanged(position: Int, isCheck: Boolean) {

                filesAdapter.ischeck[position] = isCheck
                val num = ckCount(filesAdapter.ischeck)

                //标题栏内容改变
                count.text = "已选择${num}个"

                //选择超过1个 重命名按钮消失
                if (num != 1) rename_toolbox.visibility = View.GONE
                else rename_toolbox.visibility = View.VISIBLE

            }

        })//设置长按点击事件
    }

    private fun refreshFileList() {
        lifecycleScope.launch {
            try {
                Glide.with(this@FileActivity).load(R.mipmap.loding).into(loding_iv)
                loding_iv.visibility = View.VISIBLE
                thisFileStorage = UserHelper.getFile(thisPath)//获取这一级的数据
                subFileStorageList = mSort(thisFileStorage!!.subItems)//排序 子文件

                thisFileStorage?.let {
                    setAdapter()
                }

                filesAdapter.subItemList = subFileStorageList!!
                filesAdapter.ischeck = MutableList(subFileStorageList!!.size) { i -> false }
                filesAdapter.notifyDataSetChanged()

                loding_iv.visibility = View.GONE
            } catch (e: ConnectException) {
                e.printStackTrace()
                //无网络提示
                netErr(this@FileActivity)
            } catch (e: Exception) {
                e.printStackTrace()
                //测试提示
                "其他异常".showToast(this@FileActivity)
            }

        }
    }

    private fun mSort(list: List<MyItem>): List<MyItem> = (list.filter { it.itemType == "DIR" }
        .sortedBy { it.itemName }
        .plus(list.filter { it.itemType == "FILE" }
            .sortedBy { it.itemName })).toMutableList()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            resultCode == Activity.RESULT_OK && requestCode == 1 -> {
                refreshFileList()
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        refreshFileList()
    }
}