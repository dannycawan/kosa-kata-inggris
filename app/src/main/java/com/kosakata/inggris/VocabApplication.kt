package com.kosakata.inggris

import android.app.Application
import com.google.android.gms.ads.MobileAds

class VocabApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this) {}
    }
}
