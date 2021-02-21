package com.qzl.cloudalbum.other

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.qzl.cloudalbum.R
import kotlinx.android.synthetic.main.title_layout.view.*

class ToolTitleLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.tooltitle_layout, this)

    }
}