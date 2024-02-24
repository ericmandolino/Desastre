package com.swirlfist.desastre.data

import kotlinx.coroutines.CoroutineDispatcher

interface CoroutineDispatcherProvider {
    fun getMain(): CoroutineDispatcher
    fun getIO(): CoroutineDispatcher
}