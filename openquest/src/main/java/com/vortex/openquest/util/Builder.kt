package com.vortex.openquest.util

/*
* MIT License
*
* Copyright (c) 2019 Rodrigo Lopes Martins
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*/

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

            if (path.isNullOrEmpty() && baseUrl.isNullOrEmpty()) {
                throw IllegalArgumentException("You need to provide a url!")
            }

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