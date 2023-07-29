package com.swirlfist.desastre

import android.content.Context
import androidx.room.Room
import com.swirlfist.desastre.data.CoroutineDispatcherProvider
import com.swirlfist.desastre.data.ITodoRepository
import com.swirlfist.desastre.data.TodoRepository
import com.swirlfist.desastre.data.db.TodoDao
import com.swirlfist.desastre.data.db.TodoDatabase
import com.swirlfist.desastre.data.useCase.IAddTodoUseCase
import com.swirlfist.desastre.data.useCase.IObserveTodoListUseCase
import com.swirlfist.desastre.data.useCase.IRemoveTodoUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

    @Singleton
    @Provides
    fun provideCoroutineDispatcherProvider(): CoroutineDispatcherProvider = CoroutineDispatcherProvider()

    @Singleton
    @Provides
    fun provideTodoDatabase(@ApplicationContext context: Context): TodoDatabase =
        Room
            .databaseBuilder(context, TodoDatabase::class.java, "todo_database")
            .build()

    @Singleton
    @Provides
    fun provideTodoDao(todoDatabase: TodoDatabase): TodoDao =
        todoDatabase.TodoDao()

    @Singleton
    @Provides
    fun provideTodoRepository(todoDao: TodoDao, coroutineDispatcherProvider: CoroutineDispatcherProvider): ITodoRepository {
        return TodoRepository(todoDao, coroutineDispatcherProvider.getIO())
    }

    @Singleton
    @Provides
    fun provideObserveTodoListUseCase(todoRepository: ITodoRepository): IObserveTodoListUseCase = IObserveTodoListUseCase(todoRepository::observeTodos)

    @Singleton
    @Provides
    fun provideAddTodoUseCase(todoRepository: ITodoRepository): IAddTodoUseCase = IAddTodoUseCase(todoRepository::addTodo)

    @Singleton
    @Provides
    fun provideRemoveTodoUseCase(todoRepository: ITodoRepository): IRemoveTodoUseCase = IRemoveTodoUseCase(todoRepository::removeTodo)
}