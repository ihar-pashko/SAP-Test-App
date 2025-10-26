package com.sap.codelab.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sap.codelab.data.database.AppDatabase.Companion.DATABASE_VERSION
import com.sap.codelab.data.model.MemoModel

/**
 * That database that is used to store information.
 */
@Database(
    entities = [MemoModel::class],
    version = DATABASE_VERSION,
    exportSchema = false
)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun getMemoDao(): MemoDao

    companion object {
        const val DATABASE_NAME = "codelab"
        const val DATABASE_VERSION = 1
    }
}
