package com.vortex.openquest

import com.vortex.openquest.adapter.GsonAdapterFactory
import com.vortex.openquest.contracts.ConverterAdapter
import com.vortex.openquest.contracts.RequestCommand
import com.vortex.openquest.processor.RequestProcessor
import com.vortex.openquest.util.Request
import com.vortex.openquest.util.Response

object Openquest {

    var converterAdapter: ConverterAdapter = GsonAdapterFactory().create()
    private var request: Request? = null

    fun setup(
        converterAdapter: ConverterAdapter? = null,
        request: Request? = null
    ): Openquest {
        converterAdapter?.let { this.converterAdapter = it }
        this.request = request
        return this
    }

    suspend fun <R : Any> processRequest(requestCommand: RequestCommand): Response<R> {
        request?.let {
            requestCommand.request.baseUrl = it.baseUrl
        }
        return requestCommand.execute()
    }
}

