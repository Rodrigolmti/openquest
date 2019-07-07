package com.vortex.openquest.util

import java.net.MalformedURLException
import java.net.URL

data class Builder(
    var path: String? = null,
    var baseUrl: String? = null,
    var requestBody: Any? = null,
    var verifyName: Boolean = false,
    var readTimeout: Int = defaultTimeout,
    var connectionTimeout: Int = defaultTimeout,
    var headers: MutableMap<String, String> = hashMapOf(),
    var pathParams: MutableMap<String, String> = hashMapOf()
) {

    @Throws(IllegalArgumentException::class)
    fun getConnectionUrl(): URL {
        try {

            val stringBuilder = StringBuilder()

            pathParams.takeIf { it.isNotEmpty() }?.let { params ->
                params.forEach { (key, value) ->
                    stringBuilder.append(if (stringBuilder.isEmpty()) "?$key=$value" else "&$key=$value")
                }
            }

            var url = baseUrl?.let { base ->
                path?.let { path ->
                    base + path
                } ?: run {
                    base
                }
            } ?: run {
                path?.let { path ->
                    path
                }
            }

            url?.let {
                if (stringBuilder.isNotEmpty()) {
                    url += stringBuilder.toString()
                }
                return URL(url)
            }

        } catch (error: MalformedURLException) {
            throw IllegalArgumentException("If you wanna use only the pathUrl, you must provide a full path into it!")
        }

        throw IllegalArgumentException("You must provide a baseUrl or pathUrl or both!")
    }
}

fun build(block: Builder.() -> Unit): Builder = Builder().apply(block)