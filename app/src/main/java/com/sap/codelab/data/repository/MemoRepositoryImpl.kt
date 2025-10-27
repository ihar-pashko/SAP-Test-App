package com.sap.codelab.data.repository

import android.util.Log
import com.sap.codelab.data.database.MemoDao
import com.sap.codelab.data.mapper.MemoMapper
import com.sap.codelab.domain.interfaces.MemoRepository
import com.sap.codelab.domain.model.Memo
import com.sap.codelab.presentation.notifications.GeofenceHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * The repository is used to retrieve data from a data source.
 */
internal class MemoRepositoryImpl(
    private val memoDao: MemoDao,
    private val mapper: MemoMapper,
    private val ioDispatcher: CoroutineDispatcher,
    private val geofenceHelper: GeofenceHelper
) : MemoRepository {

    override suspend fun saveMemo(memo: Memo): Result<Long> = withContext(ioDispatcher) {
        try {
            val memoModel = mapper.fromUIToModel(memo)
            val insertedId = memoDao.insert(memoModel)
            Result.success(insertedId)
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
            if (memoModel != null) {
                val memo = mapper.fromModelToUI(memoModel)
                Result.success(memo)
            } else {
                Result.failure(NoSuchElementException("Memo with id $id not found"))
            }
        } catch (e: Exception) {
            Log.e("MemoRepository", "Error getting memo by ID: $id", e)
            Result.failure(e)
        }
    }

    override suspend fun addGeofenceForMemo(memo: Memo, radius: Float): Result<Unit> = withContext(ioDispatcher) {
        if (memo.id > 0 && memo.reminderLatitude != null && memo.reminderLongitude != null) {
            try {
                geofenceHelper.addGeofence(
                    id = memo.id.toString(),
                    latitude = memo.reminderLatitude,
                    longitude = memo.reminderLongitude,
                    radius = radius
                )
                Result.success(Unit)
            } catch (e: SecurityException) {
                Log.e("MemoRepository", "Missing location permissions for adding geofence ${memo.id}", e)
                Result.failure(e)
            } catch (e: Exception) {
                Log.e("MemoRepository", "Error adding geofence for memo ${memo.id}", e)
                Result.failure(e)
            }
        } else {
            Log.d("MemoRepository", "Skipping geofence add for memo ${memo.id} - no location or invalid ID")
            Result.success(Unit)
        }
    }

    override suspend fun removeGeofenceForMemo(memoId: Long): Result<Unit> = withContext(ioDispatcher) {
        if (memoId > 0) {
            try {
                geofenceHelper.removeGeofence(memoId.toString())
                Result.success(Unit)
            } catch (e: Exception) {
                Log.w("MemoRepository", "Error removing geofence for memo $memoId", e)
                Result.failure(e)
            }
        } else {
            Result.success(Unit)
        }
    }
}
