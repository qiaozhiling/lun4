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
import com.qzl.cloudalbum.internet.CldAbService
import com.qzl.cloudalbum.internet.MyItem
import com.qzl.cloudalbum.internet.NetHelper.await
import com.qzl.cloudalbum.internet.ServiceCreator
import com.qzl.cloudalbum.other.UserHelper
import com.qzl.cloudalbum.other.showToast
import com.qzl.cloudalbum.other.showToastOnUi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.DateFormat.getDateTimeInstance

class FilesAdapter(
    private var subItemList: List<MyItem>,
    private val thisPath: String,
    private val context: Context
) :
    RecyclerView.Adapter<FilesAdapter.FileItemHolder>() {
    var isShowed = false
    var canChecked = false

    inner class FileItemHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnLongClickListener, View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

        val fileItemImage: ImageView = itemView.findViewById(R.id.image_dLitem)//图
        val fileItemName: TextView = itemView.findViewById(R.id.name_dLitem)//名字Tv
        val fileItemTime: TextView = itemView.findViewById(R.id.time_fileitem)//时间Tview(没做)
        val fileItemCheckbox: CheckBox = itemView.findViewById(R.id.checkbox_fileitem)//选框Checkbox

        var mposition: Int = 0//列表位置
        var subItem: MyItem? = null//这个子项
        var subItemPath: String = ""//子项路径

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
            fileItemCheckbox.setOnCheckedChangeListener(this)
        }

        //长按
        override fun onLongClick(v: View?): Boolean {
            if (onItemClickListener != null) {
                return onItemClickListener!!.setOnItemLongClick(mposition)
            }
            return false
        }

        //点击
        override fun onClick(v: View?) {
            if (onItemClickListener != null) {
                return onItemClickListener!!.setOnItemClick(mposition, subItemPath, subItem)
            }

        }

        //选中
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
        canChecked = false

        val item = subItemList.get(position)//子项json对象
        val itemName = item.itemName//子项名字
        val itemType = item.itemType//子项类型

        if (itemType == "DIR") {//是文件夹
            holder.fileItemImage.setImageResource(R.mipmap.dir)
            holder.fileItemTime.text = "DIR"
        } else if (itemType == "FILE") {//图片
            holder.fileItemTime.text = "FILE"
            val url =
                "http://39.104.71.38:8080${item.file?.thumbnailURL}"

            val header = LazyHeaders.Builder().addHeader("cookie", UserHelper.getCookie()).build()
            Glide.with(context).load(GlideUrl(url, header)).into(holder.fileItemImage)
        }

        if (isShowed) {
            holder.fileItemCheckbox.visibility = View.VISIBLE
        } else {
            holder.fileItemCheckbox.visibility = View.GONE
        }

        if (item.hidden) holder.fileItemName.setTextColor(Color.GRAY)
        else holder.fileItemName.setTextColor(Color.BLACK)

        holder.fileItemCheckbox.isChecked = subItemList[position].getCheckedStatus()//选框选中
        holder.mposition = position//位置
        holder.fileItemName.text = itemName//显示内容 子项名字
        holder.subItemPath = "$thisPath/$itemName"//子项路径
        holder.subItem = subItemList[position]

        canChecked = true
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    interface OnItemClickListener {
        fun setOnItemClick(
            position: Int,
            subItemPath: String,
            subItem: MyItem?
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
    @Throws(Exception::class)
    suspend fun delete() {
        val list = getTargetItemsPath(getCheckedItems())

        try {
            val myResult =
                ServiceCreator.create(CldAbService::class.java).delete(list)
                    .await(context)
            ////
        } catch (e: Exception) {
            throw e
        }
    }

    //重命名
    @Throws(Exception::class)
    suspend fun rename(newName: String): Boolean {
        try {
            val list = getCheckedItems()
            if (list.size == 1) {
                val item = list.first()
                val oldPath = getTargetItemsPath(item)

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

                val data = ServiceCreator.create(CldAbService::class.java)
                    .fileRename(oldPath, newNameWithExtension).await(context)

                return true
            } else {
                "文件数出错".showToast(context)
                return false
            }
        } catch (e: Exception) {
            throw e
        }
    }

    //保存选中文件
    @Throws(Exception::class)
    suspend fun download() {
        val list = getCheckedItems()
        if (list.isEmpty()) {
            "请选择文件".showToast(context)
        } else {
            withContext(Dispatchers.IO) {

                "开始保存".showToastOnUi(context)
                for (item in list) {

                    launch(Dispatchers.IO) {
                        try {
                            val url = item.file?.fileURL

                            //是文件 有url
                            url?.let {
                                val name = item.itemName//文件名
                                val path =
                                    context.getExternalFilesDir(null)?.path + "/" + name//本地路径
                                val fileUrl = "http://39.104.71.38:8080$url"//文件url

                                //插入数据库 获取主键
                                val dLPDao = AppDatabase.getDatabase(context).getDLPicDao()
                                val dPic =
                                    DownloadPic(UserHelper.getEmail(), name, "下载中...", path, false)
                                val id = dLPDao.dLPicInsert(dPic)

                                //获取file
                                val header =
                                    LazyHeaders.Builder()
                                        .addHeader("Cookie", UserHelper.getCookie())
                                        .build()
                                val file =
                                    Glide.with(context).downloadOnly()
                                        .load(GlideUrl(fileUrl, header))
                                        .submit().get()

                                //保存到本地
                                saveFile(file, path)

                                //更新数据库
                                dPic.id = id
                                dPic.success = true
                                val createDate =
                                    getDateTimeInstance().format(System.currentTimeMillis()) //设置时间格式
                                dPic.dLTime = createDate
                                dLPDao.dLPicUpdate(dPic)

                                //提示成功
                                "${name}保存成功".showToastOnUi(context)

                            }

                        } catch (e: Exception) {
                            throw e
                        }
                    }

                }
            }
        }

    }

    //改变选中文件隐藏状态
    @Throws(Exception::class)
    suspend fun changeFileState() {
        val list = getTargetItemsPath(getCheckedItems())
        if (list.isEmpty()) {
            "请选择文件".showToast(context)
        } else {
            try {
                val result = ServiceCreator
                    .create(CldAbService::class.java).changeHideStatus(list).await(context)

                ////
            } catch (e: Exception) {
                throw e
            }
        }

    }

    //分享文件
    @Throws(Exception::class)
    suspend fun shareFile() {
        val list = getTargetItemsPath(getCheckedItems())
        if (list.isEmpty()) {
            "请选择文件".showToast(context)
        } else {
            try {
                val shareCode = ServiceCreator
                    .create(CldAbService::class.java).share(list).await(context)
                UserHelper.copyIntoClipBoard(shareCode, context)
                "分享码已保存至剪贴板".showToastOnUi(context)
            } catch (e: Exception) {
                throw e
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    //保存file
    private fun saveFile(file: File?, pathToSvae: String) {
        val fis = FileInputStream(file)
        val fos = FileOutputStream(pathToSvae)

        val bytes = ByteArray(fis.available())

        fis.read(bytes)
        fos.write(bytes)
        file?.delete()
    }

    //返回选中的子项列表（”/root/{...}")
    fun getCheckedItems(): List<MyItem> = subItemList.filter { it.getCheckedStatus() }

    //子项路径列表（”/root/{...}")
    private fun getTargetItemsPath(list: List<MyItem>): List<String> =
        list.map { "${thisPath}/${it.itemName}" }

    //子项路径列表（”/root/{...}")
    private fun getTargetItemsPath(item: MyItem): String = "${thisPath}/${item.itemName}"

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //重置 全选 全不选 ischecked 是否全选
    fun setSubItemListCheck(isChecked: Boolean) {
        for (i in subItemList)
            i.setCheck(isChecked)
    }

    //跟新Items数据
    fun reSetItemList(new: List<MyItem>) {
        subItemList = new
    }

    fun getSubItemList(): List<MyItem> {
        return subItemList
    }

    fun setItemChecked(position: Int) {
        subItemList[position].changeCheckedStatus()
    }

}