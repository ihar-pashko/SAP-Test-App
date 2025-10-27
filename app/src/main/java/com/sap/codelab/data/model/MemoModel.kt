package com.sap.codelab.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a memo.
 */
@Entity(
    tableName = "memo",
    indices = [Index("isDone")]
)
data class MemoModel(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Long,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "description")
    var description: String,
    @ColumnInfo(name = "reminderLatitude")
    var reminderLatitude: Double? = null,
    @ColumnInfo(name = "reminderLongitude")
    var reminderLongitude: Double? = null,
    @ColumnInfo(name = "isDone")
    var isDone: Boolean = false
)
