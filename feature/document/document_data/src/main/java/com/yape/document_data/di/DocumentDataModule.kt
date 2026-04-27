package com.yape.document_data.di

import com.yape.document_data.local.storage.DocumentLocalStorage
import com.yape.document_data.repository.DocumentRepositoryImpl
import com.yape.document_domain.repository.DocumentRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val documentDataModule = module {
    single {
        DocumentLocalStorage(context = androidContext(), secureFileFacade = get())
    }
    single<DocumentRepository> {
        DocumentRepositoryImpl(context = androidContext(), documentLocalStorage = get())
    }
}
