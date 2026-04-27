package com.yape.common.di

import com.yape.common.helper.DocumentFileInputStreamHelper
import com.yape.common.helper.ImageWatermarkHelper
import com.yape.common.helper.SecureFileFacade
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val commonModule = module {
    single {
        SecureFileFacade(context = androidContext())
    }
    single {
        DocumentFileInputStreamHelper(context = androidContext(), secureFileFacade = get())
    }
    single {
        ImageWatermarkHelper(context = androidContext())
    }
}
