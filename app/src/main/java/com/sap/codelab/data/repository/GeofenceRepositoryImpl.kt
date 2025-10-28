package com.sap.codelab.data.repository

import android.util.Log
import com.sap.codelab.data.database.MemoDao
import com.sap.codelab.data.mapper.MemoMapper
import com.sap.codelab.domain.interfaces.GeofenceRepository
import com.sap.codelab.presentation.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class GeofenceRepositoryImpl(
    private val memoDao: MemoDao,
    private val mapper: MemoMapper,
    private val notificationHelper: NotificationHelper,
    private val ioDispatcher: CoroutineDispatcher,
) : GeofenceRepository {

    override suspend fun handleGeofenceTransition(geofenceIds: List<String>) = withContext(ioDispatcher) {
        geofenceIds.forEach { id ->
            val memoId = id.toLongOrNull() ?: return@forEach
            val memoModel = memoDao.getMemoById(memoId)
            if (memoModel != null) {
                val memo = mapper.fromModelToUI(memoModel)
                notificationHelper.showMemoNotification(memo)
            } else {
                Log.w("GeofenceRepo", "Memo not found for geofence $id")
            }
        }
    }
}
