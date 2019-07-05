package com.vortex.openquest.util

sealed class Response<out R : Any> {
    class Success<R : Any>(
        val statusCode: Int,
        val data: R
    ) : Response<R>()
    class Failure(val error: Error) : Response<Nothing>()
}

sealed class Error {
    object Unknown : Error()
}
