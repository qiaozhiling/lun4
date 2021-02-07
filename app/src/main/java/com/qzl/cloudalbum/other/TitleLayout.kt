package com.qzl.cloudalbum.other

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.qzl.cloudalbum.R
import kotlinx.android.synthetic.main.title_layout.view.*

class TitleLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.title_layout, this)

        titleBack.setOnClickListener {
            val activity = context as Activity
            activity.finish()
        }


    }
}