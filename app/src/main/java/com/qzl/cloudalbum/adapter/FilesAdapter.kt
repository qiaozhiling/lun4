package com.qzl.cloudalbum.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.database.AppDatabase
import com.qzl.cloudalbum.database.DownloadPic
import com.qzl.cloudalbum.internet.MyItem
import com.qzl.cloudalbum.other.UserHelper
import com.qzl.cloudalbum.other.netErr
import com.qzl.cloudalbum.other.showToastOnUi
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.ConnectException
import java.text.DateFormat.getDateTimeInstance

class FilesAdapter(
    var subItemList: List<MyItem>,
    var ischeck: MutableList<Boolean>,
    val thisPath: String,
    val context: Context
) :
    RecyclerView.Adapter<FilesAdapter.FileItemHolder>() {
    var isShowed = false

    inner class FileItemHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnLongClickListener, View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

        val image_fileitem: ImageView = itemView.findViewById(R.id.image_dLitem)//图
        val name_fileitem: TextView = itemView.findViewById(R.id.name_dLitem)//名字Tv
        val time_fileitem: TextView = itemView.findViewById(R.id.time_fileitem)//时间Tview
        val checkbox_fileitem: CheckBox = itemView.findViewById(R.id.checkbox_fileitem)//选中Checkbox

        var thisFileStorage: MyItem? = null//这个子项
        var mposition: Int = 0//列表位置
        var subItemPath: String = ""//子项路径

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
            checkbox_fileitem.setOnCheckedChangeListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
            if (onItemClickListener != null) {
                return onItemClickListener!!.setOnItemLongClick(mposition)
            }
            return false
        }

        override fun onClick(v: View?) {
            if (onItemClickListener != null) {
                return onItemClickListener!!.setOnItemClick(mposition, subItemPath, thisFileStorage)
            }

        }

        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            if (onItemClickListener != null) {
                return onItemClickListener!!.setOnItemCheckedChanged(mposition, isChecked)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileItemHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fileitem_layout, parent, false)

        return FileItemHolder(view)
    }

    override fun getItemCount(): Int = subItemList.size

    override fun onBindViewHolder(holder: FileItemHolder, position: Int) {

        val item = subItemList.get(position)//子项json对象
        val itemName = item.itemName//子项名字
        val itemType = item.itemType//子项类型

        if (itemType == "DIR") {//是文件夹
            holder.image_fileitem.setImageResource(R.mipmap.dir)
            holder.time_fileitem.text = "DIR"
        } else if (itemType == "FILE") {//图片
            holder.time_fileitem.text = "FILE"
            val url =
                "http://39.104.71.38:8080${item.file?.thumbnailURL}"

            val header = LazyHeaders.Builder().addHeader("cookie", UserHelper.getCookie()).build()
            Glide.with(context).load(GlideUrl(url, header)).into(holder.image_fileitem)
        }

        if (isShowed) {
            holder.checkbox_fileitem.visibility = View.VISIBLE
        } else {
            holder.checkbox_fileitem.visibility = View.GONE
        }

        if (item.hidden) holder.name_fileitem.setTextColor(Color.GRAY)
        else holder.name_fileitem.setTextColor(Color.BLACK)

        holder.checkbox_fileitem.isChecked = ischeck[position]
        holder.mposition = position//位置
        holder.name_fileitem.text = itemName//显示内容 子项名字
        holder.subItemPath = "$thisPath/$itemName"//子项路径
        holder.thisFileStorage = subItemList[position]

    }

    ////////////////////////////////////////////////////////////////////////////////////////
    interface OnItemClickListener {
        fun setOnItemClick(
            position: Int,
            subItemPath: String,
            fileStorage: MyItem?
        )

        fun setOnItemLongClick(position: Int): Boolean
        fun setOnItemCheckedChanged(position: Int, isCheck: Boolean)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }
    //////////////////////////////////////////////////////////////////////////////////////////

    //删除选中
    suspend fun delete() {
        val list =  mutableListOf<String>()
        for ((position, checked) in ischeck.withIndex()) {
            if (checked) {
                val item = subItemList[position]
                val path = "${thisPath}/${item.itemName}"
                list.add(path)

            }
        }
        try {
            UserHelper.deleteFile(list)
        } catch (e: ConnectException) {
            e.printStackTrace()
            netErr(context)
        } catch (e: Exception) {
            e.printStackTrace()
            //test
            "其他异常".showToastOnUi(context)
        }
    }

    //重命名
    suspend fun rename(newName: String): Boolean {
        try {
            for ((position, checked) in ischeck.withIndex()) {
                if (checked) {
                    val item = subItemList[position]
                    val oldPath = "${thisPath}/${item.itemName}"

                    //后缀
                    val extension = if (item.itemType == "DIR") {
                        ""
                    } else {
                        when {
                            oldPath.endsWith(".gif") -> ".gif"
                            oldPath.endsWith(".png") -> ".png"
                            oldPath.endsWith(".jpeg") -> ".jpeg"
                            else -> ".jpg"
                        }
                    }

                    val newNameWithExtension = "$newName$extension"

                    return UserHelper.reName(oldPath, newNameWithExtension)
                }
            }
            return false
        } catch (e: Exception) {
            throw e
        }
    }

    //保存选中文件
    suspend fun download() {
        withContext(Dispatchers.IO) {

            for ((position, checked) in ischeck.withIndex()) {
                if (checked) {
                    launch(Dispatchers.IO) {
                        try {
                            val item = subItemList[position]
                            val url = item.file?.fileURL


                            //是文件 有url
                            url?.let {
                                val name = item.itemName
                                val path = context.getExternalFilesDir(null)?.path + "/" + name
                                val fileUrl = "http://39.104.71.38:8080$url"

                                val dPic =
                                    DownloadPic(UserHelper.getEmail(), name, "下载中", path, false)
                                val dLPDao = AppDatabase.getDatabase(context).getDLPicDao()

                                val header =
                                    LazyHeaders.Builder()
                                        .addHeader("Cookie", UserHelper.getCookie())
                                        .build()

                                val id = dLPDao.dLPicInsert(dPic)

                                val file =
                                    Glide.with(context).downloadOnly()
                                        .load(GlideUrl(fileUrl, header))
                                        .submit().get()

                                val fis = FileInputStream(file)
                                val fos = FileOutputStream(path)

                                val bytes = ByteArray(fis.available())

                                fis.read(bytes)
                                fos.write(bytes)
                                file?.delete()

                                dPic.id = id
                                dPic.success = true
                                val createDate =
                                    getDateTimeInstance().format(System.currentTimeMillis()) //设置时间格式
                                dPic.dLTime = createDate
                                dLPDao.dLPicUpdate(dPic)

                                "${name}保存成功".showToastOnUi(context)

                            }

                        } catch (e: ConnectException) {
                            e.printStackTrace()
                            netErr(context)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            //test
                            "其他异常".showToastOnUi(context)
                        }
                    }
                }
            }
        }


    }

    //改变选中文件隐藏状态
    suspend fun changeFileState() {
        val list = mutableListOf<String>()
        for ((position, checked) in ischeck.withIndex()) {
            if (checked) {
                val item = subItemList[position]
                val path = "${thisPath}/${item.itemName}"
                list.add(path)

            }
        }
        try {
            UserHelper.changeStatus(list)
        } catch (e: ConnectException) {
            e.printStackTrace()
            netErr(context)
        } catch (e: Exception) {
            e.printStackTrace()
            //test
            "其他异常".showToastOnUi(context)
        }
    }


    //
    fun savePic(file: File, name: String, path: String): Boolean {

        return true
    }

    //

}