package com.sap.codelab.utils.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T> Flow<T>.collectInLifecycle(
    fragment: Fragment,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    action: suspend (value: T) -> Unit
) {
    fragment.viewLifecycleOwner.lifecycleScope.launch {
        fragment.viewLifecycleOwner.repeatOnLifecycle(state) {
            collect(action)
        }
    }
}
