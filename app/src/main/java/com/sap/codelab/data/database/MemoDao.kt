package com.sap.codelab.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sap.codelab.data.model.MemoModel

/**
 * The Dao representation of a Memo.
 */
@Dao
internal interface MemoDao {

    /**
     * @return all memos that are currently in the database.
     */
    @Query("SELECT * FROM memo")
    suspend fun getAll(): List<MemoModel>

    /**
     * @return all memos that are currently in the database and have not yet been marked as "done".
     */
    @Query("SELECT * FROM memo WHERE isDone = 0")
    suspend fun getOpen(): List<MemoModel>

    /**
     * Inserts the given Memo into the database. We currently do not support updating of memos.
     */
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(memoModel: MemoModel): Long

    /**
     * @return the memo whose id matches the given id.
     */
    @Query("SELECT * FROM memo WHERE id = :memoId")
    suspend fun getMemoById(memoId: Long): MemoModel?
}
