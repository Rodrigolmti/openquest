package com.vortex.openquest

import com.vortex.openquest.adapter.GsonAdapterFactory
import com.vortex.openquest.contracts.ConverterAdapter
import com.vortex.openquest.contracts.RequestCommand
import com.vortex.openquest.config.Response
import com.vortex.openquest.util.Request

object Openquest {

    var converterAdapter: ConverterAdapter = GsonAdapterFactory().create()
    private var request: Request? = null

    fun setup(
        converterAdapter: ConverterAdapter? = null,
        request: Request
    ): Openquest {
        converterAdapter?.let { this.converterAdapter = it }
        this.request = request
        return this
    }

    suspend fun <R : Any> processRequest(requestCommand: RequestCommand): Response<R> {
        request.let {


        }

        return requestCommand.execute()
    }
}


