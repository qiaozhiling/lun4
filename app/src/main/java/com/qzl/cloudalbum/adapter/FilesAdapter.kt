package com.qzl.cloudalbum.adapter

import android.content.Context
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
import com.qzl.cloudalbum.other.UserHelper
import org.json.JSONObject

class FilesAdapter(
    val subItems: MutableList<JSONObject>,
    var ischeck: MutableList<Boolean>,
    val thisPath: String,
    val context: Context
) :
    RecyclerView.Adapter<FilesAdapter.FileItemHolder>() {
    var isShowed = false

    inner class FileItemHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnLongClickListener, View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {

        val image_fileitem: ImageView = itemView.findViewById(R.id.image_fileitem)//图
        val name_fileitem: TextView = itemView.findViewById(R.id.name_fileitem)//子项名字
        val checkbox_fileitem: CheckBox = itemView.findViewById(R.id.checkbox_fileitem)//

        var isChecked = false//checkbox
        var mposition: Int = 0//
        var fileType: String = ""//文件类型
        var subItemPath: String = ""//子项路径
        var subItemName: String = ""//子项路径
        var picUrl: String = ""//图片路径

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
                return onItemClickListener!!.setOnItemClick(
                    mposition,
                    fileType,
                    subItemPath,
                    subItemName,
                    picUrl
                )
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

    override fun getItemCount(): Int = subItems.size

    override fun onBindViewHolder(holder: FileItemHolder, position: Int) {

        val item = subItems.get(position)//子项json对象
        val itemName = item.getString("itemName")//子项名字
        val itemType = item.getString("itemType")//子项类型

        if (itemType == "DIR") {//是文件夹
            holder.image_fileitem.setImageResource(R.mipmap.dir)
        } else if (itemType == "FILE") {//图片
            val url =
                "http://39.104.71.38:8080${item.getJSONObject("file").getString("thumbnailURL")}"

            val header = LazyHeaders.Builder().addHeader("cookie", UserHelper.getCookie()).build()

            holder.picUrl =
                "http://39.104.71.38:8080${item.getJSONObject("file").getString("fileURL")}"

            Glide.with(context)
                .load(GlideUrl(url, header))
                .into(holder.image_fileitem)
        }

        if (isShowed) {
            holder.checkbox_fileitem.visibility = View.VISIBLE
        } else {
            holder.checkbox_fileitem.visibility = View.GONE
        }
        holder.checkbox_fileitem.isChecked = ischeck[position]
        holder.mposition = position//位置
        holder.name_fileitem.text = itemName//显示内容 子项名字
        holder.subItemPath = "$thisPath/$itemName"//子项路径
        holder.subItemName = itemName//子项名字
        holder.fileType = itemType//子项类型
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    interface OnItemClickListener {
        fun setOnItemClick(
            position: Int,
            type: String,
            subItemPath: String,
            subItemName: String,
            picUrl: String/*, isCheck: Boolean*/
        )

        fun setOnItemLongClick(position: Int): Boolean
        fun setOnItemCheckedChanged(position: Int, isCheck: Boolean)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }
    //////////////////////////////////////////////////////////////////////////////////////////

    //  删除数据
    /*fun delete() {
        for ((position, i) in ischeck.withIndex()) {
            if (i) {
                subItems.removeAt(position)
                notifyItemRemoved(position)
            }
        }

        //删除动画

        notifyDataSetChanged()
    }
*/
    fun rename() {}

    fun download() {}
}