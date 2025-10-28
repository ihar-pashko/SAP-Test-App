package com.sap.codelab

import android.app.Application
import com.sap.codelab.di.databaseModule
import com.sap.codelab.di.dispatcherModule
import com.sap.codelab.di.domainMapperModule
import com.sap.codelab.di.helperModule
import com.sap.codelab.di.notificationModule
import com.sap.codelab.di.repositoryModule
import com.sap.codelab.di.uiMapperModule
import com.sap.codelab.di.useCaseModule
import com.sap.codelab.di.viewModelModule
import com.sap.codelab.presentation.notifications.NotificationHelper
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Extension of the Android Application class.
 */
internal class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    databaseModule,
                    repositoryModule,
                    viewModelModule,
                    domainMapperModule,
                    useCaseModule,
                    dispatcherModule,
                    uiMapperModule,
                    notificationModule,
                    helperModule
                )
            )

            koin.get<NotificationHelper>().createNotificationChannel()
        }
    }
}
