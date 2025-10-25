package com.sap.codelab

import android.app.Application
import com.sap.codelab.di.databaseModule
import com.sap.codelab.di.memoRepositoryImplModule
import com.sap.codelab.di.viewModelModule
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
                    memoRepositoryImplModule,
                    viewModelModule
                )
            )
        }
    }
}