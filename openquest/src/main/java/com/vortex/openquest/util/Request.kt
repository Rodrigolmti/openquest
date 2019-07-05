package com.vortex.openquest.util

import java.net.MalformedURLException
import java.net.URL

data class Request(
    var baseUrl: String? = null,
    var pathUrl: String? = null,
    var requestBody: Any? = null,
    var readTimeout: Int = defaultTimeout,
    var connectionTimeout: Int = defaultTimeout
) {

    @Throws(IllegalArgumentException::class)
    fun getConnectionUrl(): URL {

        try {

            baseUrl?.let { base ->

                pathUrl?.let { path ->
                    val url = base + path
                    return URL(url)
                } ?: run {
                    return URL(base)
                }

            } ?: run {

                pathUrl?.let { path ->
                    return URL(path)
                }

            }

        } catch (error: MalformedURLException) {
            throw IllegalArgumentException("If you wanna use only the pathUrl, you must provide a full path into it!")
        }

        throw IllegalArgumentException("You must provide a baseUrl or pathUrl or both!")
    }

}

fun build(block: Request.() -> Unit): Request = Request().apply(block)