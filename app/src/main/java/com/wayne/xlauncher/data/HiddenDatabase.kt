/* © 2024 Wayne Zhu. All rights reserved. */
package com.wayne.xlauncher.data

import android.content.Context
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

const val dbName = "HiddenItemDB"

@Entity
data class HiddenItem(
    @PrimaryKey val packageName: String,
    @ColumnInfo(name = "code") val code: String
)

@Dao
interface HiddenItemDao {
    @Query("SELECT * FROM hiddenItem")
    fun getAll(): List<HiddenItem>

    @Insert
    fun insertAll(vararg items: HiddenItem)

    @Delete
    fun delete(user: HiddenItem)
}

@Database(entities = [HiddenItem::class], version = 1)
abstract class HiddenItemDatabase : RoomDatabase() {
    abstract fun hiddenItemDao(): HiddenItemDao
}

fun addHiddenItem(packageName: String, code: String, context: Context) {
    val db = Room.databaseBuilder(
        context,
        HiddenItemDatabase::class.java, dbName
    ).build()
    db.hiddenItemDao().insertAll(HiddenItem(packageName, code))
}

fun getAllHiddenItem(context: Context): List<HiddenItem> {
    val db = Room.databaseBuilder(
        context,
        HiddenItemDatabase::class.java, dbName
    ).build()
    return db.hiddenItemDao().getAll()
}
