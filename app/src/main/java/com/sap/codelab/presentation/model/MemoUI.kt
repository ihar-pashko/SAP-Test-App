package com.sap.codelab.presentation.model

import com.sap.codelab.utils.extensions.empty

data class MemoUI(
    val id: Long = 0,
    val title: String = String.Companion.empty,
    val description: String = String.Companion.empty,
    val isDone: Boolean = false
)
