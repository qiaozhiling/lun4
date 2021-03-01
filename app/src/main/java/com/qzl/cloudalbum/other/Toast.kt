package com.qzl.cloudalbum.other

import android.content.Context
import android.widget.Toast

fun String.showToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

fun netErr(context: Context){
    Toast.makeText(context, "请检查网络", Toast.LENGTH_SHORT).show()
}