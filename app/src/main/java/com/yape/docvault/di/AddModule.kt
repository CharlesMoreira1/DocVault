package com.yape.docvault.di

import com.yape.common.di.commonModule
import com.yape.document_data.di.documentDataModule
import com.yape.document_domain.di.documentDomainModule
import com.yape.detail_presentation.di.detailPresentationModule
import com.yape.home_presentation.di.homePresentationModule

val addModule = listOf(
    commonModule,
    documentDataModule,
    documentDomainModule,
    homePresentationModule,
    detailPresentationModule
)
