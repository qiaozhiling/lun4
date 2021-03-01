package com.qzl.cloudalbum.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, entities = [DownloadPic::class])
abstract class AppDatabase : RoomDatabase() {

    abstract fun getDLPicDao(): DownloadPicDao

    companion object {

        private var instance: AppDatabase? = null//缓存AppDatabase

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {

            instance?.let {
                //instance不为null返回instance
                return it
            }

            /*val appDatabase = Room.databaseBuilder(//构建AppDatabase实例
                context.applicationContext,
                AppDatabase::class.java,//AppDatabase的Class类型
                "user_Database"//数据库名
            ).build()
            return appDatabase*/

            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app_database"
            ).build().apply { instance = this }
        }
    }
}