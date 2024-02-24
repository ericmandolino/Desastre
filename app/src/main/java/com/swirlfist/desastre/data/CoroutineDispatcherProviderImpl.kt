package com.swirlfist.desastre.data

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class CoroutineDispatcherProviderImpl @Inject constructor() : CoroutineDispatcherProvider {
    override fun getMain() = Dispatchers.Main

    override fun getIO() = Dispatchers.IO
}