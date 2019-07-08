package com.vortex.openquest.processor

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

import com.vortex.openquest.contracts.RequestCommand
import com.vortex.openquest.util.Builder
import com.vortex.openquest.util.Error
import com.vortex.openquest.util.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import javax.net.ssl.HttpsURLConnection
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class RequestProcessor(
    private val requestCommand: RequestCommand
) {

    suspend operator fun <R : Any> invoke(): Response<R> = withContext(Dispatchers.IO) {
        suspendCoroutine<Response<R>> { continuation ->

            var connection: HttpURLConnection? = null

            try {

                initialValidations(requestCommand.builder)
                val url = requestCommand.builder.getConnectionUrl()

                connection = if (url.protocol == "https") url.openConnection() as HttpsURLConnection else url.openConnection() as HttpURLConnection
                connection.connectTimeout = requestCommand.builder.connectionTimeout
                connection.readTimeout = requestCommand.builder.readTimeout
                connection.requestMethod = requestCommand.identifier.name

                requestCommand.builder.headers.takeIf { it.isNotEmpty() }?.let { headers ->
                    headers.forEach { (key, value) ->
                        connection.setRequestProperty(key, value)
                    }
                }

                requestCommand.converterAdapter?.let { converter ->
                    requestCommand.builder.requestBody?.let { body ->
                        connection.doOutput = true
                        connection.outputStream.use { outputStream ->
                            outputStream.write(converter.serializeBody(body))
                        }
                    }

                    connection.connect()

                    val response = converter.serializeResponse<R>(connection.inputStream)
                    response?.let {
                        continuation.resume(Response.Success(connection.responseCode, response))
                    } ?: run {
                        continuation.resume(Response.Failure(Error.Unknown))
                    }

                } ?: run {
                    continuation.resume(Response.Failure(Error.InvalidConverterAdpter))
                }

            } catch (error: IOException) {
                continuation.resumeWithException(error)
                error.printStackTrace()
            } finally {
                connection?.inputStream?.close()
                connection?.disconnect()
            }
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun initialValidations(builder: Builder) {
        if (builder.path.isNullOrEmpty() && builder.baseUrl.isNullOrEmpty()) {
            throw IllegalArgumentException("You need to provide a url!")
        }
    }
}
