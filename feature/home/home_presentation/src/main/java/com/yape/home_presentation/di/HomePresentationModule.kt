package com.yape.home_presentation.di

import com.yape.home_presentation.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val homePresentationModule = module {
    viewModel {
        HomeViewModel(
            getDocumentsUseCase = get(),
            addDocumentUseCase = get(),
            deleteDocumentUseCase = get(),
            imageWatermarkHelper = get()
        )
    }
}
