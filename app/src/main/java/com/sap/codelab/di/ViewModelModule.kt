package com.sap.codelab.di

import com.sap.codelab.presentation.create.CreateMemoViewModel
import com.sap.codelab.presentation.detail.ViewMemoViewModel
import com.sap.codelab.presentation.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        HomeViewModel(
            getAllMemosUseCase = get(),
            getOpenMemosUseCase = get(),
            updateMemoDoneStatusUseCase = get(),
            getMemoByIdUseCase = get(),
            memoUIMapper = get()
        )
    }

    viewModel {
        ViewMemoViewModel(
            getMemoByIdUseCase = get(),
            uiMapper = get()
        )
    }

    viewModel {
        CreateMemoViewModel(
            saveMemoUseCase = get(),
            validateMemoUseCase = get()
        )
    }
}
