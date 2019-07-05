package com.vortex.networking

import android.app.Application
import com.vortex.openquest.Openquest
import com.vortex.openquest.util.build

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Openquest.setup(request = build {
            baseUrl = "https://jsonplaceholder.typicode.com"
        })
    }
}