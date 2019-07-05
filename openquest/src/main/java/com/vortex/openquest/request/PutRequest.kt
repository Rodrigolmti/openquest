package com.vortex.openquest.request

import com.google.gson.GsonBuilder
import com.vortex.openquest.*
import com.vortex.openquest.contracts.RequestCommand
import com.vortex.openquest.util.Error
import com.vortex.openquest.util.Response
import com.vortex.openquest.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class PutRequest(override var request: Request) : RequestCommand {

    override suspend fun <R : Any> execute(): Response<R> = withContext(Dispatchers.IO) {
        suspendCoroutine<Response<R>> { continuation ->

            var connection: HttpsURLConnection? = null

            try {

                initialValidations(request)

                val url = request.getConnectionUrl()
                val json = GsonBuilder().create().toJson(request.requestBody)

                connection = setupHttpsConnection(url, request, RequestType.PUT)
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