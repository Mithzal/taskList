package com.example.td2.model

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room

@Database(entities = [Task::class], version =1, exportSchema = false)
abstract class ListDatabase : RoomDatabase(){

    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var Instance : ListDatabase? = null

        fun getDatabase(context : Context): ListDatabase{
            return Instance ?:synchronized(this) {
                Room.databaseBuilder(context, ListDatabase::class.java, "task_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }

}