package com.sap.codelab.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.sap.codelab.domain.interfaces.GeofenceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class GeofenceBroadcastReceiver() : BroadcastReceiver(), KoinComponent {
    private val geofenceRepository: GeofenceRepository by inject()

    private val ioDispatcher: CoroutineDispatcher by inject(
        qualifier = named(Dispatchers.IO::class.simpleName.orEmpty())
    )

    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent)
        if (event == null || event.hasError()) {
            Log.e("GeofenceReceiver", "Invalid geofence event")
            return
        }

        if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val ids = event.triggeringGeofences?.map { it.requestId } ?: return
            val pendingResult = goAsync()
            CoroutineScope(ioDispatcher).launch {
                geofenceRepository.handleGeofenceTransition(ids)
                pendingResult.finish()
            }
        }
    }
}
