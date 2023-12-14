package com.example.client.roomDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDAO {

    @Query("SELECT * FROM User")
    fun getAll(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user: User)

    @Delete
    fun delete(user: User)

    @Update
    fun update(user: User)

    @Query("DELETE FROM User")
    fun deleteAll()

    @Query("SELECT * FROM User WHERE email = :email")
    fun getUserByEmail(email: String): User?
}