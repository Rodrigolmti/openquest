package com.vortex.openquest.contracts

import com.vortex.openquest.config.Response
import com.vortex.openquest.util.Request

interface RequestCommand {
    suspend fun <R : Any> execute(): Response<R>
    var request: Request
}
