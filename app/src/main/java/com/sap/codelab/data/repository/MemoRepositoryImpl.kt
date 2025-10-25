package com.sap.codelab.data.repository

import androidx.annotation.WorkerThread
import com.sap.codelab.data.database.MemoDao
import com.sap.codelab.data.mapper.MemoMapper
import com.sap.codelab.domain.interfaces.MemoRepository
import com.sap.codelab.domain.model.Memo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The repository is used to retrieve data from a data source.
 */
internal class MemoRepositoryImpl(
    private val getMemoDao: MemoDao,
    private val mapper: MemoMapper
) : MemoRepository {

    @WorkerThread
    override suspend fun saveMemo(memo: Memo) = withContext(Dispatchers.IO) {
        val memoModel = mapper.fromUIToModel(memo)
        getMemoDao.insert(memoModel)
    }

    @WorkerThread
    override suspend fun getOpen(): List<Memo> = withContext(Dispatchers.IO) {
        getMemoDao.getOpen().map { mapper.fromModelToUI(it) }
    }

    @WorkerThread
    override suspend fun getAll(): List<Memo> = withContext(Dispatchers.IO) {
        getMemoDao.getAll().map { mapper.fromModelToUI(it) }
    }

    @WorkerThread
    override suspend fun getMemoById(id: Long): Memo = withContext(Dispatchers.IO) {
        mapper.fromModelToUI(getMemoDao.getMemoById(id))
    }
}
