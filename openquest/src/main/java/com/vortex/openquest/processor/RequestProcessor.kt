package com.vortex.openquest.processor

import com.google.gson.GsonBuilder
import com.vortex.openquest.contracts.RequestCommand
import com.vortex.openquest.util.Builder
import com.vortex.openquest.util.Error
import com.vortex.openquest.util.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
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

                requestCommand.builder.requestBody?.let { body ->
                    connection.doOutput = true
                    val json = GsonBuilder().create().toJson(body)
                    connection.outputStream.use { outputStream ->
                        val input = json.toByteArray(Charsets.UTF_8)
                        outputStream.write(input)
                    }
                }

                connection.connect()

                requestCommand.converterAdapter?.let { converter ->
                    val response = converter.serializeResponse<R>(connection.inputStream)
                    response?.let {
                        continuation.resume(Response.Success(connection.responseCode, response))
                    } ?: run {
                        continuation.resume(Response.Failure(Error.Unknown))
                    }
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
