package com.qzl.cloudalbum.database

import androidx.room.*

@Dao
interface DLPicDao {
    @Insert
    fun dLPicInsert(dLPic: DLPic): Long//返回生成的主键

    @Update
    fun dLPicUpdate(dLPic: DLPic)

    @Delete
    fun dLPicDelete(dLPic: DLPic)

    @Query("select * from DLPic")//查找所有User
    fun loadAllDLPics(): List<DLPic>//返回所有User的列表

    @Query("select * from DLPic where id= :id")
    fun loadDLPicById(id: Long): DLPic
}