package com.vortex.openquest.contracts

import java.io.InputStream

interface ConverterAdapter {
    fun create(): ConverterAdapter
    fun <R> serializeResponse(inputStream: InputStream?): R?
}