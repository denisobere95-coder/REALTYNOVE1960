package com.denis.realtynova

import android.app.Application
import androidx.appfunctions.service.AppFunctionConfiguration
import com.denis.realtynova.core.ai.RealtyNovaFunctions
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class RealtyNovaApp : Application(), AppFunctionConfiguration.Provider {

    @Inject
    lateinit var realtyNovaFunctions: RealtyNovaFunctions

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    override val appFunctionConfiguration: AppFunctionConfiguration by lazy {
        AppFunctionConfiguration.Builder()
            .addEnclosingClassFactory(RealtyNovaFunctions::class.java) { realtyNovaFunctions }
            .build()
    }
}
