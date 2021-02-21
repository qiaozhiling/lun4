package com.qzl.cloudalbum.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.qzl.cloudalbum.R
import org.json.JSONArray

class FilesAdapter(val subItems: JSONArray, val context: Context) :
    RecyclerView.Adapter<FilesAdapter.FileItemHolder>() {

    inner class FileItemHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnLongClickListener, View.OnClickListener {
        val image_fileitem: ImageView = itemView.findViewById(R.id.image_fileitem)
        val name_fileitem: TextView = itemView.findViewById(R.id.name_fileitem)
        val checkbox_fileitem: CheckBox = itemView.findViewById(R.id.checkbox_fileitem)
        var mposition: Int=0

        override fun onLongClick(v: View?): Boolean {
            TODO("Not yet implemented")
        }

        override fun onClick(v: View?) {
            TODO("Not yet implemented")
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileItemHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.fileitem_layout, parent, false)

        view.setOnClickListener {
            Toast.makeText(parent.context, "d", Toast.LENGTH_SHORT).show()
        }

        return FileItemHolder(view)
    }

    override fun getItemCount(): Int = subItems.length()

    override fun onBindViewHolder(holder: FileItemHolder, position: Int) {
        holder.mposition = position
        val item = subItems.getJSONObject(position)
        val itemName = item.getString("itemName")
        val itemType = item.getString("itemType")
        holder.name_fileitem.text = itemName

    }

    interface OnItemClickListener {
        fun setOnItemClick(position: Int, isCheck: Boolean)
        fun setOnItemLongClick(position: Int): Boolean
        fun setOnItemCheckedChanged(position: Int, isCheck: Boolean)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

}