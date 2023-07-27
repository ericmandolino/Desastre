package com.swirlfist.desastre.data

import kotlinx.coroutines.Dispatchers

class CoroutineDispatcherProvider {
    fun getIO() = Dispatchers.IO
}