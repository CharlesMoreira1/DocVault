package com.yape.document_domain.di

import com.yape.document_domain.usecase.AddDocumentUseCase
import com.yape.document_domain.usecase.DeleteDocumentUseCase
import com.yape.document_domain.usecase.GetDocumentsUseCase
import com.yape.document_domain.usecase.RecordAccessUseCase
import org.koin.dsl.module

val documentDomainModule = module {
    factory {
        GetDocumentsUseCase(repository = get())
    }
    factory {
        AddDocumentUseCase(repository = get())
    }
    factory {
        DeleteDocumentUseCase(repository = get())
    }
    factory {
        RecordAccessUseCase(repository = get())
    }
}
