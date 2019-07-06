package com.vortex.openquest.request

import com.vortex.openquest.contracts.ConverterAdapter
import com.vortex.openquest.contracts.RequestCommand
import com.vortex.openquest.processor.RequestProcessor
import com.vortex.openquest.util.Builder
import com.vortex.openquest.util.RequestType
import com.vortex.openquest.util.Response

class PostRequest(
    override var builder: Builder
) : RequestCommand {

    override suspend fun <R : Any> execute(): Response<R> = RequestProcessor(this).invoke()

    override var identifier: RequestType = RequestType.POST

    override var converterAdapter: ConverterAdapter? = null
}