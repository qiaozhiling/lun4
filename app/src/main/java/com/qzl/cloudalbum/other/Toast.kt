package com.qzl.cloudalbum.other

import android.content.Context
import android.os.Looper
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

suspend fun String.showToastOnUi(context: Context) {
    val a = this
    withContext(Dispatchers.Main) {
        Toast.makeText(context, a, Toast.LENGTH_SHORT).show()
    }
}

fun String.showToast(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

suspend fun netErr(context: Context) {
    withContext(Dispatchers.Main){
        Toast.makeText(context, "请检查网络", Toast.LENGTH_SHORT).show()
    }
}