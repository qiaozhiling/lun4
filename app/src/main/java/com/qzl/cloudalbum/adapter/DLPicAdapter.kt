package com.qzl.cloudalbum.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.qzl.cloudalbum.R
import com.qzl.cloudalbum.database.DownloadPic

class DLPicAdapter(
    var picList: List<DownloadPic>,
    val context: Context
) :
    RecyclerView.Adapter<DLPicAdapter.DownloadPicHolder>() {

    inner class DownloadPicHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnLongClickListener
        , View.OnClickListener {
        val image_fileitem: ImageView = itemView.findViewById(R.id.image_dLitem)//图
        val name_fileitem: TextView = itemView.findViewById(R.id.name_dLitem)//名字Tv
        val time_fileitem: TextView = itemView.findViewById(R.id.time_fileitem)//时间Tview
        var mposition: Int = 0//列表位置
        var pic: DownloadPic? = null

        init {
            itemView.setOnLongClickListener(this)
            itemView.setOnClickListener(this)
        }

        override fun onLongClick(v: View?): Boolean {
            if (onItemClickListener != null) {
                return onItemClickListener!!.setOnItemLongClick(mposition)
            }
            return false
        }

        override fun onClick(v: View?) {
            if (onItemClickListener != null) {
                return onItemClickListener!!.setOnItemClick(mposition, pic)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadPicHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fileitem_layout, parent, false)

        return DownloadPicHolder(view)
    }

    override fun getItemCount(): Int = picList.size

    override fun onBindViewHolder(holder: DownloadPicHolder, position: Int) {
        val pic = picList[position]//图片信息对象
        holder.pic = pic
        holder.name_fileitem.text = pic.name
        holder.time_fileitem.text = pic.dLTime
        Glide.with(context).load(pic.localPath).into(holder.image_fileitem)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    interface OnItemClickListener {
        fun setOnItemLongClick(position: Int): Boolean
        fun setOnItemClick(
            position: Int, pic: DownloadPic?
        )
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }
}