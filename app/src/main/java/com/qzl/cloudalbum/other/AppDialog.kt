package com.qzl.cloudalbum.other

import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.qzl.cloudalbum.R

class AppDialog(context: Context) : AlertDialog(context) {

    private var commit_but: Button
    private var cancel_but: Button
    private var dir_title: TextView
    private var create_hide_Cb: CheckBox
    private var dir_name_Et: EditText


    init {
        val view = View.inflate(context, R.layout.dialog, null)
        commit_but = view.findViewById(R.id.commit_but) as Button
        cancel_but = view.findViewById(R.id.cancel_but) as Button
        dir_title = view.findViewById(R.id.dir_title) as TextView
        dir_name_Et = view.findViewById(R.id.dir_name_Et) as EditText
        create_hide_Cb = view.findViewById(R.id.create_hide_Cb) as CheckBox
        this.setView(view)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun setmTitle(s: String): AppDialog {
        dir_title.text = s
        return this
    }

    fun showCheckBox(show: Boolean): AppDialog {
        create_hide_Cb.visibility = if (show) View.VISIBLE else View.GONE
        return this
    }

    fun getCheckState() = create_hide_Cb.isChecked

    fun setPositiveButton(l: (v: View) -> Unit) {
        commit_but.setOnClickListener(l)
    }

    fun setNegativeButton(l: (v: View) -> Unit) {
        cancel_but.setOnClickListener(l)
    }

    fun getText() = dir_name_Et.text.toString()
}