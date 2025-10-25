package com.sap.codelab.domain

import com.sap.codelab.data.model.MemoModel

/**
 * Interface for a repository offering memo related CRUD operations.
 */
internal interface MemoRepository {

    /**
     * Saves the given memo to the database.
     */
    fun saveMemo(memoModel: MemoModel)

    /**
     * @return all memos currently in the database.
     */
    fun getAll(): List<MemoModel>

    /**
     * @return all memos currently in the database, except those that have been marked as "done".
     */
    fun getOpen(): List<MemoModel>

    /**
     * @return the memo whose id matches the given id.
     */
    fun getMemoById(id: Long): MemoModel
}