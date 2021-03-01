package com.qzl.cloudalbum.database

import androidx.room.*

@Dao
interface DownloadPicDao {
    //增
    @Insert
    fun dLPicInsert(dLPic: DownloadPic): Long//返回生成的主键

    //删
    @Delete
    suspend fun dLPicDelete(dLPic: DownloadPic)

    //改
    @Update
    suspend fun dLPicUpdate(dLPic: DownloadPic)

    //查
    @Query("select * from DownloadPic")//查找所有DownloadPic
    suspend fun loadAllDLPics(): List<DownloadPic>//返回所有DownloadPic的列表

    //查
    @Query("select *from DownloadPic where email=:email")//查找DownloadPic
    suspend fun loadDLPics(email: String): List<DownloadPic>//返回DownloadPic的列表


    @Query("select * from DownloadPic where id= :id")
    suspend fun loadDLPicById(id: Long): DownloadPic
}