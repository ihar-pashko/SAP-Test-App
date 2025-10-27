package com.sap.codelab.domain.interfaces

import com.sap.codelab.domain.model.Memo

/**
 * Interface for a repository offering memo related CRUD operations.
 */
interface MemoRepository {

    /**
     * Saves the given memo to the database.
     */
    suspend fun saveMemo(memo: Memo): Result<Long>

    /**
     * @return all memos currently in the database.
     */
    suspend fun getAll(): Result<List<Memo>>

    /**
     * @return all memos currently in the database, except those that have been marked as "done".
     */
    suspend fun getOpen(): Result<List<Memo>>

    /**
     * @return the memo whose id matches the given id.
     */
    suspend fun getMemoById(id: Long): Result<Memo>

    suspend fun addGeofenceForMemo(memo: Memo): Result<Unit>
    suspend fun removeGeofenceForMemo(memoId: Long): Result<Unit>
}
