package com.vortex.openquest.util

data class Request(
    var baseUrl: String? = null,
    var pathUrl: String? = null,
    var requestBody: Any? = null,
    var readTimeout: Int = defaultTimeout,
    var connectionTimeout: Int = defaultTimeout
)

fun build(block: Request.() -> Unit): Request = Request().apply(block)