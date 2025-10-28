package com.sap.codelab.domain.interfaces

interface GeofenceRepository {
    suspend fun handleGeofenceTransition(geofenceIds: List<String>)
}
