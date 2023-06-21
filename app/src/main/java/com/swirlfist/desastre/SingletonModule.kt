package com.swirlfist.desastre

import com.swirlfist.desastre.data.ITodoRepository
import com.swirlfist.desastre.data.TodoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

    @Singleton
    @Provides
    fun provideTodoRepository(): ITodoRepository {
        return TodoRepository()
    }
}