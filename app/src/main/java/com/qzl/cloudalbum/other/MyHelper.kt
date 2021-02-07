package com.qzl.cloudalbum.other

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.activities.ActivityCollector.finishAll

object MyHelper {

    fun setMyPopMenu(context: Context, view: View) {
        val popMenu = PopupMenu(context, view)//菜单
        popMenu.menuInflater.inflate(R.menu.file_title_menu, popMenu.menu)//填充
        view.setOnClickListener {
            popMenu.show()
        }//设置点击标题栏菜单图标 显示菜单
        popMenu.setOnMenuItemClickListener{
            when(it.itemId){
                R.id.logOut_item -> finishAll()
            }//菜单内部点击事件
            return@setOnMenuItemClickListener  true
        }
    }
}