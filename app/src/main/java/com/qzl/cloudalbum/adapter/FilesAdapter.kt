package com.qzl.cloudalbum.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
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
import com.qzl.cloudalbum.other.showToast
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.ConnectException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

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
        } else if (itemType == "FILE") {//图片
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

        for ((position, checked) in ischeck.withIndex()) {
            try {
                if (checked) {
                    val item = subItemList[position]
                    val path = "${thisPath}/${item.itemName}"
                    UserHelper.deleteFile(path)
                }
            } catch (e: ConnectException) {
                e.printStackTrace()
                netErr(context)
            } catch (e: Exception) {
                e.printStackTrace()
                //test
                "其他异常".showToast(context)
            }

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
    fun download() {
        for ((position, checked) in ischeck.withIndex()) {
            if (checked) {
                thread {
                    try {
                        val item = subItemList[position]
                        val name = item.itemName
                        val url = item.file?.fileURL
                        //是文件 有url
                        url?.let {
                            val fileUrl = "http://39.104.71.38:8080$url"
                            val path = context.getExternalFilesDir(null)?.path + "/" + name
                            val dPic = DownloadPic(UserHelper.getEmail(), name, "", path, false)

                            val dLPDao = AppDatabase.getDatabase(context).getDLPicDao()

                            val header =
                                LazyHeaders.Builder()
                                    .addHeader("Cookie", UserHelper.getCookie())
                                    .build()

                            val file =
                                Glide.with(context).downloadOnly()
                                    .load(GlideUrl(fileUrl, header))
                                    .submit().get()

                            /*val storePath: String =
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .getPath().toString()
                            val fos2 = FileOutputStream(storePath + "/" + name)*/

                            val fis = FileInputStream(file)
                            val fos1 =
                                FileOutputStream(path)

                            val bytes = ByteArray(fis.available())

                            Log.i("out", "start")
                            fis.read(bytes)
                            fos1.write(bytes)
                            file?.delete()
                            Log.i("out", "over")

                            val formatter =
                                SimpleDateFormat("YYYY-MM-dd HH:mm:ss") //设置时间格式
                            formatter.setTimeZone(TimeZone.getTimeZone("GMT+08")) //设置时区
                            val curDate = Date(System.currentTimeMillis()) //获取当前时间
                            val createDate: String = formatter.format(curDate) //格式转换

                            dLPDao.dLPicInsert(dPic)

                            /*context.sendBroadcast(
                                Intent(
                                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                    Uri.fromFile(File(context.getExternalFilesDir(null)?.path + "/a.jpg"))
                                )
                            )*/
                            Log.i("outtttt", path)
                            "${name}保存成功".showToast(context)
                        }

                    } catch (e: ConnectException) {
                        e.printStackTrace()
                        netErr(context)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        //test
                        "其他异常".showToast(context)
                    }
                }
            }
        }

    }

    //改变选中文件隐藏状态
    suspend fun changeFileState() {
        try {
            for ((position, checked) in ischeck.withIndex()) {
                if (checked) {
                    val item = subItemList[position]
                    UserHelper.changeStatus("${thisPath}/${item.itemName}")
                }
            }

        } catch (e: Exception) {
            throw e
        }
    }

    //

}