package com.sap.codelab.domain.model

data class Memo(
    var id: Long,
    var title: String,
    var description: String,
    var reminderDate: Long,
    var reminderLatitude: Long,
    var reminderLongitude: Long,
    var isDone: Boolean = false
)
