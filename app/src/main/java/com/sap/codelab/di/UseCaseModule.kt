package com.sap.codelab.di

import com.sap.codelab.domain.usecases.AddGeofenceForMemoUseCase
import com.sap.codelab.domain.usecases.GetAllMemosUseCase
import com.sap.codelab.domain.usecases.GetMemoByIdUseCase
import com.sap.codelab.domain.usecases.GetOpenMemosUseCase
import com.sap.codelab.domain.usecases.RemoveGeofenceForMemoUseCase
import com.sap.codelab.domain.usecases.SaveMemoUseCase
import com.sap.codelab.domain.usecases.UpdateMemoDoneStatusUseCase
import com.sap.codelab.domain.usecases.ValidateMemoUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetOpenMemosUseCase(repository = get()) }
    factory { GetAllMemosUseCase(repository = get()) }
    factory { GetMemoByIdUseCase(repository = get()) }
    factory { SaveMemoUseCase(repository = get()) }
    factory { UpdateMemoDoneStatusUseCase(repository = get()) }
    factory { ValidateMemoUseCase() }
    factory { AddGeofenceForMemoUseCase(repository = get()) }
    factory { RemoveGeofenceForMemoUseCase(repository = get()) }
}
