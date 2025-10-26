package com.sap.codelab.data.repository

import android.util.Log
import com.sap.codelab.data.database.MemoDao
import com.sap.codelab.data.mapper.MemoMapper
import com.sap.codelab.domain.interfaces.MemoRepository
import com.sap.codelab.domain.model.Memo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * The repository is used to retrieve data from a data source.
 */
internal class MemoRepositoryImpl(
    private val memoDao: MemoDao,
    private val mapper: MemoMapper,
    private val ioDispatcher: CoroutineDispatcher
) : MemoRepository {

    override suspend fun saveMemo(memo: Memo): Result<Unit> = withContext(ioDispatcher) {
        try {
            val memoModel = mapper.fromUIToModel(memo)
            memoDao.insert(memoModel)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("MemoRepository", "Error saving memo", e)
            Result.failure(e)
        }
    }

    override suspend fun getOpen(): Result<List<Memo>> = withContext(ioDispatcher) {
        try {
            val memos = memoDao.getOpen().map { mapper.fromModelToUI(it) }
            Result.success(memos)
        } catch (e: Exception) {
            Log.e("MemoRepository", "Error getting open memos", e)
            Result.failure(e)
        }
    }

    override suspend fun getAll(): Result<List<Memo>> = withContext(ioDispatcher) {
        try {
            val memos = memoDao.getAll().map { mapper.fromModelToUI(it) }
            Result.success(memos)
        } catch (e: Exception) {
            Log.e("MemoRepository", "Error getting all memos", e)
            Result.failure(e)
        }
    }

    override suspend fun getMemoById(id: Long): Result<Memo> = withContext(ioDispatcher) {
        try {
            val memoModel = memoDao.getMemoById(id)
            val memo = mapper.fromModelToUI(memoModel)
            Result.success(memo)
        } catch (e: Exception) {
            Log.e("MemoRepository", "Error getting memo by ID: $id", e)
            Result.failure(e)
        }
    }
}
