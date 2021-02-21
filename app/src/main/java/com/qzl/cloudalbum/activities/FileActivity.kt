package com.qzl.cloudalbum.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.adapter.FilesAdapter
import com.qzl.cloudalbum.internet.CldAbService
import com.qzl.cloudalbum.internet.LoginReception
import com.qzl.cloudalbum.internet.ServiceCreator
import com.qzl.cloudalbum.other.Helper
import kotlinx.android.synthetic.main.activity_file.*
import kotlinx.android.synthetic.main.title_layout.view.*
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.ConnectException

class FileActivity : BaseActivity() {
    private lateinit var thisPath: String//    /root
    private lateinit var thisName: String//    root

    private lateinit var filesAdapter: FilesAdapter
    private lateinit var subItem: JSONArray

    private val service: CldAbService = ServiceCreator.create(CldAbService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file)
        supportActionBar?.hide()
        //设置菜单
        setMyPopMenu(this, mtitle.titleMeun)
        val sPf: SharedPreferences = getSharedPreferences("login_setting", Context.MODE_PRIVATE)

        //危
        thisPath = intent.getStringExtra("thisPath")!!
        thisName = intent.getStringExtra("thisName")!!
        subItem = JSONArray(intent.getStringExtra("subItemJson")!!)
        mtitle.titleText.text = thisName

        Log.i("intent", thisPath + "----" + thisName)

        filesAdapter = FilesAdapter(subItem,this)
        rv_files.layoutManager = LinearLayoutManager(this)
        rv_files.adapter = filesAdapter


    }

    private fun getFile(path: String) {
        var response: Response<ResponseBody>? = null
        val t = Thread {
            try {
                response = service.getFileItem(Helper.getCookie(), path).clone().execute()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        t.start()
        t.join()

        if (response == null) {
            Log.i("getFile", "网络链接失败")
        } else if (response!!.code() == 200) {

            val json = response!!.body()!!.string()
            val data = JSONObject(json).getJSONObject("data")
            subItem = data.getJSONArray("subItems")
        } else if (response!!.code() != 200) {
            Log.i("getFile", "cookie失效")
        }

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

                R.id.logOut_item -> {//注销账号
                    context.getSharedPreferences("login_setting", AppCompatActivity.MODE_PRIVATE)
                        .edit().clear().apply()
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    ActivityCollector.finishAll()
                }
            }//菜单内部点击事件
            return@setOnMenuItemClickListener true
        }
    }
}