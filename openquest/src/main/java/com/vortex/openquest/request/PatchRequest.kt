package com.vortex.openquest.request

import com.vortex.openquest.contracts.RequestCommand
import com.vortex.openquest.processor.RequestProcessor
import com.vortex.openquest.util.Request
import com.vortex.openquest.util.RequestType
import com.vortex.openquest.util.Response

class PatchRequest(
    override var request: Request
) : RequestCommand {

    override suspend fun <R : Any> execute(): Response<R> = RequestProcessor(this, RequestType.PATCH).invoke()
}