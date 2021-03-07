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
        RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        val fileItemImage: ImageView = itemView.findViewById(R.id.image_dLitem)//图
        val fileItemName: TextView = itemView.findViewById(R.id.name_dLitem)//名字Tv
        val fileItemTime: TextView = itemView.findViewById(R.id.time_fileitem)//时间Tview
        private var mposition: Int = 0//列表位置
        var pic: DownloadPic? = null

        init {
            itemView.setOnLongClickListener(this)
            itemView.setOnClickListener(this)
        }


        override fun onClick(v: View?) {
            if (onItemClickListener != null) {
                return onItemClickListener!!.setOnItemClick(mposition, pic)
            }
        }

        override fun onLongClick(v: View?): Boolean {
            if (onItemClickListener != null) {
                return onItemClickListener!!.setOnItemLongClick(mposition, pic, itemView)
            }
            return false
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
        holder.fileItemName.text = pic.name
        holder.fileItemTime.text = pic.dLTime
        Glide.with(context).asBitmap().load(pic.localPath).into(holder.fileItemImage)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    interface OnItemClickListener {
        fun setOnItemClick(
            position: Int,
            pic: DownloadPic?
        )

        fun setOnItemLongClick(
            position: Int,
            pic: DownloadPic?,
            itemView: View
        ): Boolean
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

}