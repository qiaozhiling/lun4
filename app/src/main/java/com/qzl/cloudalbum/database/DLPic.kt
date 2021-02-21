package com.qzl.cloudalbum.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.*


@Entity
data class DLPic(var name: String, var dLTime: String, var localPath: String) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}