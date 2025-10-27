package com.sap.codelab.di

import com.sap.codelab.receiver.GeofenceBroadcastReceiver
import org.koin.dsl.module

val receiverModule = module {
    single { GeofenceBroadcastReceiver() }
}
