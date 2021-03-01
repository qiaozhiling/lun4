package com.qzl.cloudalbum.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity
data class DownloadPic(
    var email: String,//用户email
    var name: String,//文件名
    var dLTime: String,//下载时间
    var localPath: String,//本地位置
    var success: Boolean//下载成功
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}