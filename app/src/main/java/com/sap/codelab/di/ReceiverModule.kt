package com.sap.codelab.di

import com.sap.codelab.services.GeofenceBroadcastReceiver
import org.koin.dsl.module

val receiverModule = module {
    single { GeofenceBroadcastReceiver() }
}
