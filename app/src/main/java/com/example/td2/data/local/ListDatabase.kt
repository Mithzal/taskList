package com.example.td2.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version =3, exportSchema = false)
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