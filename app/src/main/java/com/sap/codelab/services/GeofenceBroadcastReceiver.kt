package com.sap.codelab.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.sap.codelab.domain.usecases.GetMemoByIdUseCase
import com.sap.codelab.presentation.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GeofenceBroadcastReceiver : BroadcastReceiver(), KoinComponent {
    private val getMemoByIdUseCase: GetMemoByIdUseCase by inject()
    private val notificationHelper: NotificationHelper by inject()

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent == null || geofencingEvent.hasError()) {
            val errorMessage = geofencingEvent?.errorCode ?: "Unknown error"
            Log.e("GeofenceReceiver", "Geofencing error: $errorMessage")
            return
        }

        if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.i("GeofenceReceiver", "Geofence ENTER event triggered")

            geofencingEvent.triggeringGeofences?.forEach { geofence ->
                val memoIdString = geofence.requestId
                val memoId = memoIdString.toLongOrNull()

                if (memoId != null) {
                    Log.d("GeofenceReceiver", "Processing geofence for memo ID: $memoId")
                    val pendingResult = goAsync()
                    scope.launch {
                        try {
                            getMemoByIdUseCase(memoId)
                                .onSuccess { memo ->
                                    Log.d("GeofenceReceiver", "Memo found: ${memo.title}")
                                    notificationHelper.showMemoNotification(memo)
                                }
                                .onFailure { error ->
                                    Log.e("GeofenceReceiver", "Error getting memo for notification ID: $memoId", error)
                                }
                        } finally {
                            pendingResult.finish()
                        }
                    }
                } else {
                    Log.w("GeofenceReceiver", "Invalid memo ID from geofence: $memoIdString")
                }
            }
        } else {
            Log.d("GeofenceReceiver", "Ignoring geofence transition type: ${geofencingEvent.geofenceTransition}")
        }
    }
}
