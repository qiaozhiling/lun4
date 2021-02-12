package com.qzl.cloudalbum.other

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.activities.ActivityCollector.finishAll
import com.qzl.cloudalbum.activities.LoginActivity
import com.qzl.cloudalbum.internet.IdPw
import com.qzl.cloudalbum.internet.LRData
import com.qzl.cloudalbum.internet.LoginService
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MyHelper {

    fun setMyPopMenu(context: Context, view: View) {
        val popMenu = PopupMenu(context, view)//菜单
        popMenu.menuInflater.inflate(R.menu.file_title_menu, popMenu.menu)//填充
        view.setOnClickListener {
            popMenu.show()
        }//设置点击标题栏菜单图标 显示菜单
        popMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logOut_item -> {
                    context.getSharedPreferences("login_setting", AppCompatActivity.MODE_PRIVATE)
                        .edit().clear().apply()
                    context.startActivity(Intent(context,LoginActivity::class.java))
                    finishAll()
                }
            }//菜单内部点击事件
            return@setOnMenuItemClickListener true
        }
    }

    fun sendLoginPost(server: LoginService, idPw: IdPw) {
        var data: LRData?

    }

    fun saveLSetting(idPw: IdPw, sPf: SharedPreferences, remPw: Boolean,autoLi:Boolean) {
        val editor = sPf.edit()
        editor.putString("uid", idPw.uid)
        editor.putString("paswd", idPw.paswd)
        editor.putBoolean("remPw", remPw)
        editor.putBoolean("autoLi", autoLi)
        editor.apply()
    }

}