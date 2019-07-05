package com.vortex.openquest.adapter

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.vortex.openquest.contracts.ConverterAdapter
import java.io.InputStream

class GsonAdapterFactory : ConverterAdapter {

    override fun create() = GsonAdapterFactory()

    override fun <R> serializeResponse(inputStream: InputStream?): R? {
        return inputStream?.let { stream ->
            val response = stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            GsonBuilder().create().fromJson<R>(response, object : TypeToken<R>() {}.type)
        }
    }
}