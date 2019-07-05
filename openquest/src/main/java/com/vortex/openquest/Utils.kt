package com.vortex.openquest

import com.vortex.openquest.util.Request
import com.vortex.openquest.util.RequestType
import java.net.URL
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSession

@Throws(IllegalArgumentException::class)
fun initialValidations(request: Request) {
    if (request.pathUrl.isNullOrEmpty() && request.baseUrl.isNullOrEmpty()) {
        throw IllegalArgumentException("You need to provide a url!")
    }
}

fun setupHttpsConnection(
    url: URL,
    request: Request,
    requestType: RequestType
): HttpsURLConnection? {
    val connection = url.openConnection() as HttpsURLConnection
    connection.requestMethod = requestType.name
    connection.readTimeout = request.readTimeout
    connection.doOutput = request.requestBody != null
    connection.connectTimeout = request.connectionTimeout
    return connection
}

fun hostNameVerifier(connection: HttpsURLConnection, url: URL) {
    connection.hostnameVerifier = object : HostnameVerifier {
        override fun verify(hostname: String, session: SSLSession): Boolean {
            return HttpsURLConnection.getDefaultHostnameVerifier().verify(url.host, session)
        }
    }
}