package com.sap.codelab.domain.model

data class Memo(
    val id: Long,
    val title: String,
    val description: String,
    val reminderLatitude: Double? = null,
    val reminderLongitude: Double? = null,
    val isDone: Boolean = false
)
