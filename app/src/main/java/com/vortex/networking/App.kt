package com.vortex.networking

import android.app.Application
import com.vortex.openquest.Openquest
import com.vortex.openquest_gson_adapter.GsonAdapter

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        Openquest
            .setBaseUrl("https://jsonplaceholder.typicode.com")
            .setConverterAdapter(GsonAdapter())
    }
}