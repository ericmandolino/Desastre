package com.swirlfist.desastre.data

import kotlinx.coroutines.CoroutineDispatcher

interface ICoroutineDispatcherProvider {
    fun getMain(): CoroutineDispatcher
    fun getIO(): CoroutineDispatcher
}