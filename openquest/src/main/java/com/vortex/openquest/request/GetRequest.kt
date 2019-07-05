package com.vortex.openquest.request

import com.vortex.openquest.Openquest
import com.vortex.openquest.contracts.RequestCommand
import com.vortex.openquest.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.net.ssl.HttpsURLConnection
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class GetRequest(override var request: Request) : RequestCommand {

    override suspend fun <R : Any> execute(): Response<R> = withContext(Dispatchers.IO) {
        suspendCoroutine<Response<R>> { continuation ->

            var connection: HttpsURLConnection? = null

            try {

                val url = request.getConnectionUrl()

                connection = setupHttpsConnection(url, request, RequestType.GET)
                connection?.let {

                    hostNameVerifier(it, url)
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