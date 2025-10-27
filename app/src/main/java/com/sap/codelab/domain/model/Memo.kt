package com.sap.codelab.domain.model

data class Memo(
    var id: Long,
    var title: String,
    var description: String,
    var reminderLatitude: Double? = null,
    var reminderLongitude: Double? = null,
    var isDone: Boolean = false
)
