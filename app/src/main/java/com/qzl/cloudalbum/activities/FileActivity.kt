package com.qzl.cloudalbum.activities
import com.qzl.cloudalbum.activities.ActivityCollector
import com.qzl.cloudalbum.activities.BaseActivity
import com.qzl.cloudalbum.activities.LoginActivity
import com.qzl.cloudalbum.activities.PicActivity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.adapter.FilesAdapter
import com.qzl.cloudalbum.other.UserHelper
import kotlinx.android.synthetic.main.activity_file.*
import kotlinx.android.synthetic.main.title_layout.view.*
import kotlinx.android.synthetic.main.toolbox_layout.*
import kotlinx.android.synthetic.main.tooltitle_layout.*
import org.json.JSONObject

class FileActivity : BaseActivity() {
    private lateinit var thisPath: String//    /root//这一级的路径
    private lateinit var thisName: String//    root//这一级的名字

    private lateinit var filesAdapter: FilesAdapter//adapter
    private var subItemJsonList: MutableList<JSONObject>? = null//子文件列表

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file)
        supportActionBar?.hide()
        //设置菜单
        val sPf: SharedPreferences = getSharedPreferences("login_setting", Context.MODE_PRIVATE)

        Thread {
            //判断cookie是否过期
            if (UserHelper.cookieInDate() == false) {//过期 返回登入界面
                sPf.edit().clear().apply()
                startActivity(Intent(this, LoginActivity::class.java))
                ActivityCollector.finishAll()
            }
            //未过期&没网络 无操作
        }.start()

        //危
        thisPath = intent.getStringExtra("thisPath")!!//这一级的路径
        thisName = intent.getStringExtra("thisName")!!//这一级的名字
        mtitle.titleText.text = thisName//标题为这一级的名字
        setMyPopMenu(this, mtitle.titleMeun)//设置标题栏菜单
        Log.i("intentData", thisPath + "----" + thisName)

        //recyclerView设置
        rv_files.layoutManager = LinearLayoutManager(this)//layoutManager
        Log.i("subItemJsonArray", subItemJsonList.toString())

        runOnUiThread {
            subItemJsonList = UserHelper.getFile(thisPath)//网络请求获得文件数据

            Log.i("subItemJsonArray", subItemJsonList.toString())

            if (subItemJsonList != null) {
                val checklist = MutableList(subItemJsonList!!.size) { i -> false }
                filesAdapter = FilesAdapter(subItemJsonList!!, checklist, thisPath, this)
                rv_files.adapter = filesAdapter

                filesAdapter.setOnItemListener(object : FilesAdapter.OnItemClickListener {
                    override fun setOnItemClick(
                        position: Int,
                        type: String,
                        subItemPath: String,
                        subItemName: String,
                        picUrl: String
                    ) {
                        Log.i("Onclick", "Onclick")
                        Log.i("picUrl", picUrl + "-----picUrl")

                        if (type == "DIR") {//是文件夹 跳转
                            val intent = Intent(this@FileActivity, FileActivity::class.java)
                            intent.putExtra("thisPath", subItemPath)
                            intent.putExtra("thisName", subItemName)
                            this@FileActivity.startActivity(intent)
                        } else if (type == "FILE") {//图片
                            Log.i("Onclick", "Not DIR")
                            Log.i("picUrl", picUrl)
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
                        count.text = "已选择${ckCount(filesAdapter.ischeck)}个"
                    }

                })//设置长按点击事件
            } else {
                Toast.makeText(this, "获取文件信息失败", Toast.LENGTH_SHORT).show()
            }
        }


        tool_cancel.setOnClickListener {
            toolShow(false)
        }//取消

        tool_select_all.setOnClickListener {
            if (ckCount(filesAdapter.ischeck) == filesAdapter.ischeck.size) {
                filesAdapter.ischeck = MutableList(subItemJsonList!!.size) { i -> false }
                count.text = "已选择0个"
            } else {
                filesAdapter.ischeck = MutableList(subItemJsonList!!.size) { i -> true }
                count.text = "已选择${filesAdapter.ischeck.size}个"
            }
            filesAdapter.notifyDataSetChanged()
        }//全选

        newbuild.setOnClickListener{

        }//新建文件夹

        upload.setOnClickListener{

        }//上传图片

        downloadlist.setOnClickListener{

        }//查看下载列表

        delete_toolbox.setOnClickListener {
            /*filesAdapter.delete()
            Log.i("delete","delete")*/

        }//删除

        rename_toolbox.setOnClickListener {

        }//重命名

        download_toolbox.setOnClickListener {

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

                }

                R.id.download_item -> {//查看下载

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
            mtitle.visibility = View.GONE
            mtooltitle.visibility = View.VISIBLE
            mtoolbox.visibility = View.VISIBLE
            filesAdapter.isShowed = true
            filesAdapter.ischeck = MutableList(subItemJsonList!!.size) { i -> false }
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


}