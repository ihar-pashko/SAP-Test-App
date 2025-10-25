package com.sap.codelab.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sap.codelab.data.database.DbConstants.DATABASE_VERSION
import com.sap.codelab.data.model.Memo

/**
 * That database that is used to store information.
 */
@Database(
    entities = [Memo::class],
    version = DATABASE_VERSION,
    exportSchema = false
)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun getMemoDao(): MemoDao
}