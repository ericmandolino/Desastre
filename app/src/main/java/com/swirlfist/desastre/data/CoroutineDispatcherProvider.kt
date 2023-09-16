package com.swirlfist.desastre.data

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class CoroutineDispatcherProvider @Inject constructor() : ICoroutineDispatcherProvider {
    override fun getMain() = Dispatchers.Main

    override fun getIO() = Dispatchers.IO
}