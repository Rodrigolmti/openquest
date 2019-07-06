package com.vortex.openquest.contracts

import com.vortex.openquest.util.Response
import com.vortex.openquest.util.Builder
import com.vortex.openquest.util.RequestType

interface RequestCommand {
    suspend fun <R : Any> execute(): Response<R>
    var converterAdapter: ConverterAdapter?
    var identifier: RequestType
    var builder: Builder
}
