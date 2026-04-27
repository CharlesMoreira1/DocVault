package com.yape.detail_presentation.di

import com.yape.detail_presentation.DocumentDetailViewModel

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val detailPresentationModule = module {
    viewModel { (documentId: String) ->
        DocumentDetailViewModel(
            getDocumentsUseCase = get(),
            recordAccessUseCase = get(),
            documentFileInputStreamHelper = get(),
            documentId = documentId
        )
    }
}
