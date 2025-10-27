package com.sap.codelab.di

import com.sap.codelab.presentation.notifications.NotificationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val notificationModule = module {
    single { NotificationHelper(androidContext()) }
}
