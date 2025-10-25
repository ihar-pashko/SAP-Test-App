package com.sap.codelab.data.repository

import androidx.annotation.WorkerThread
import com.sap.codelab.data.database.MemoDao
import com.sap.codelab.data.model.Memo
import com.sap.codelab.domain.IMemoRepository

/**
 * The repository is used to retrieve data from a data source.
 */
internal class Repository(
    private val getMemoDao: MemoDao
) : IMemoRepository {

    @WorkerThread
    override fun saveMemo(memo: Memo) = getMemoDao.insert(memo)

    @WorkerThread
    override fun getOpen(): List<Memo> = getMemoDao.getOpen()

    @WorkerThread
    override fun getAll(): List<Memo> = getMemoDao.getAll()

    @WorkerThread
    override fun getMemoById(id: Long): Memo = getMemoDao.getMemoById(id)
}