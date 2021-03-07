package com.qzl.cloudalbum.other

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.qzl.cloudalbum.R

class ToolTitleLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.tooltitle_layout, this)
    }
}