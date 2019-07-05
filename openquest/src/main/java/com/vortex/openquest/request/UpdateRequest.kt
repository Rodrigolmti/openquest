package com.vortex.openquest.request

import com.google.gson.GsonBuilder
import com.vortex.openquest.*
import com.vortex.openquest.contracts.RequestCommand
import com.vortex.openquest.config.Error
import com.vortex.openquest.config.Response
import com.vortex.openquest.util.Request
import com.vortex.openquest.util.RequestType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UpdateRequest(override var request: Request) : RequestCommand {

    override suspend fun <R : Any> execute(): Response<R> = withContext(Dispatchers.IO) {
        suspendCoroutine<Response<R>> { continuation ->

            var connection: HttpsURLConnection? = null

            try {

                initialValidations(request)

                val url = URL(request.baseUrl)
                val json = GsonBuilder().create().toJson(request.requestBody)

                connection = setupHttpsConnection(url, request, RequestType.UPDATE)
                connection?.let {

                    hostNameVerifier(it, url)

                    connection.outputStream.use { outputStream ->
                        val input = json.toByteArray(Charsets.UTF_8)
                        outputStream.write(input)
                    }

                    it.connect()

                    val response = Openquest.converterAdapter.serializeResponse<R>(it.inputStream)
                    response?.let {
                        continuation.resume(Response.Success(response))
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
}