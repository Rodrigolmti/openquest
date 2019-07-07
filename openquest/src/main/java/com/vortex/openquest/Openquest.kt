package com.vortex.openquest

import com.vortex.openquest.adapter.GsonAdapter
import com.vortex.openquest.contracts.ConverterAdapter
import com.vortex.openquest.contracts.RequestCommand
import com.vortex.openquest.util.Response

object Openquest {

    private var converterAdapter: ConverterAdapter = GsonAdapter()
    private var baseUrl: String? = null

    fun setBaseUrl(
        baseUrl: String
    ): Openquest {
        this.baseUrl = baseUrl
        return this
    }

    fun setConverterAdapter(
        converterAdapter: ConverterAdapter
    ): Openquest {
        converterAdapter.let { this.converterAdapter = it }
        return this
    }

    suspend fun <R : Any> doRequest(requestCommand: RequestCommand): Response<R> {
        baseUrl?.let { requestCommand.builder.baseUrl = it }
        requestCommand.converterAdapter = converterAdapter
        return requestCommand.execute()
    }
}