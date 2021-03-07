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

    private var commitBut: Button
    private var cancelBut: Button
    private var dTitle: TextView
    private var createHideCb: CheckBox
    private var nameEt: EditText


    init {
        val view = View.inflate(context, R.layout.dialog, null)
        commitBut = view.findViewById(R.id.commit_but) as Button
        cancelBut = view.findViewById(R.id.cancel_but) as Button
        dTitle = view.findViewById(R.id.dir_title) as TextView
        nameEt = view.findViewById(R.id.dir_name_Et) as EditText
        createHideCb = view.findViewById(R.id.create_hide_Cb) as CheckBox
        this.setView(view)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun setmTitle(s: String): AppDialog {
        dTitle.text = s
        return this
    }

    fun showCheckBox(show: Boolean): AppDialog {
        createHideCb.visibility = if (show) View.VISIBLE else View.GONE
        return this
    }

    fun getCheckState() = createHideCb.isChecked

    fun setPositiveButton(l: (v: View) -> Unit) {
        commitBut.setOnClickListener(l)
    }

    fun setNegativeButton(l: (v: View) -> Unit) {
        cancelBut.setOnClickListener(l)
    }

    fun getText() = nameEt.text.toString()
}