package com.vortex.openquest.processor

import com.google.gson.GsonBuilder
import com.vortex.openquest.Openquest
import com.vortex.openquest.contracts.RequestCommand
import com.vortex.openquest.util.Error
import com.vortex.openquest.util.Request
import com.vortex.openquest.util.RequestType
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
    private val requestCommand: RequestCommand,
    private val requestType: RequestType
) {

    suspend operator fun <R : Any> invoke(): Response<R> = withContext(Dispatchers.IO) {
        suspendCoroutine<Response<R>> { continuation ->

            var connection: HttpURLConnection? = null

            try {

                initialValidations(requestCommand.request)
                val url = requestCommand.request.getConnectionUrl()

                connection = if (url.protocol == "https") url.openConnection() as HttpsURLConnection else url.openConnection() as HttpURLConnection
                connection.connectTimeout = requestCommand.request.connectionTimeout
                connection.readTimeout = requestCommand.request.readTimeout
                connection.requestMethod = requestType.name

                requestCommand.request.requestBody?.let { body ->
                    connection.doOutput = true
                    val json = GsonBuilder().create().toJson(body)
                    connection.outputStream.use { outputStream ->
                        val input = json.toByteArray(Charsets.UTF_8)
                        outputStream.write(input)
                    }
                }

                connection.connect()

                val response = Openquest.converterAdapter.serializeResponse<R>(connection.inputStream)
                response?.let {
                    continuation.resume(Response.Success(connection.responseCode, response))
                } ?: run {
                    continuation.resume(Response.Failure(Error.Unknown))
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
    private fun initialValidations(request: Request) {
        if (request.pathUrl.isNullOrEmpty() && request.baseUrl.isNullOrEmpty()) {
            throw IllegalArgumentException("You need to provide a url!")
        }
    }
}
