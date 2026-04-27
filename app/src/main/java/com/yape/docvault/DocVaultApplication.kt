package com.yape.docvault

import android.app.Application
import com.yape.docvault.di.addModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level.ERROR
import org.koin.core.logger.Level.NONE
import timber.log.Timber

class DocVaultApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        setupTimber()
        setupKoin()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    private fun setupKoin() {
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) ERROR else NONE)
            androidContext(this@DocVaultApplication)
            modules(addModule)
        }
    }
}
