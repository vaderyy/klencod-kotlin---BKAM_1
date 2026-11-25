package com.webkam.app

import android.app.Application
import android.content.Context
import com.webkam.app.utils.LocaleHelper
import com.webkam.app.utils.PreferenceManager

class BakamApp : Application() {

    companion object {
        lateinit var instance: BakamApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun attachBaseContext(base: Context) {
        val lang = PreferenceManager.getLanguage(base)
        super.attachBaseContext(LocaleHelper.setLocale(base, lang))
    }
}
