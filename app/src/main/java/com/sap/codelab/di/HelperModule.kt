package com.sap.codelab.di

import com.sap.codelab.presentation.notifications.GeofenceHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val helperModule = module {
    single { GeofenceHelper(androidContext()) }
}
