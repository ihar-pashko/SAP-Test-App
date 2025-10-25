package com.sap.codelab.data.repository

import androidx.annotation.WorkerThread
import com.sap.codelab.data.database.MemoDao
import com.sap.codelab.data.model.MemoModel
import com.sap.codelab.domain.MemoRepository

/**
 * The repository is used to retrieve data from a data source.
 */
internal class MemoRepositoryImpl(
    private val getMemoDao: MemoDao
) : MemoRepository {

    @WorkerThread
    override fun saveMemo(memoModel: MemoModel) = getMemoDao.insert(memoModel)

    @WorkerThread
    override fun getOpen(): List<MemoModel> = getMemoDao.getOpen()

    @WorkerThread
    override fun getAll(): List<MemoModel> = getMemoDao.getAll()

    @WorkerThread
    override fun getMemoById(id: Long): MemoModel = getMemoDao.getMemoById(id)
}