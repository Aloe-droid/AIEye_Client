package com.example.client.roomDB

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class UserDB : RoomDatabase() {

    abstract fun userDAO(): UserDAO

    companion object {
        private var instance: UserDB? = null

        @Synchronized
        fun getInstance(context: Context): UserDB? {
            if (instance == null) {
                synchronized(UserDB::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext, UserDB::class.java, "User.DB"
                    ).build()
                }
            }
            return instance
        }
    }
}
