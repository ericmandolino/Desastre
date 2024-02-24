package com.swirlfist.desastre

import android.content.Context
import androidx.room.Room
import com.swirlfist.desastre.data.CoroutineDispatcherProvider
import com.swirlfist.desastre.data.CoroutineDispatcherProviderImpl
import com.swirlfist.desastre.data.ReminderDataSource
import com.swirlfist.desastre.data.TodoDataSource
import com.swirlfist.desastre.data.TodoRepository
import com.swirlfist.desastre.data.TodoRepositoryImpl
import com.swirlfist.desastre.data.db.ReminderDao
import com.swirlfist.desastre.data.db.ReminderDataSourceRoom
import com.swirlfist.desastre.data.db.TodoDao
import com.swirlfist.desastre.data.db.TodoDataSourceRoom
import com.swirlfist.desastre.data.db.TodoDatabase
import com.swirlfist.desastre.domain.IAddOrUpdateReminderUseCase
import com.swirlfist.desastre.domain.IAddOrUpdateTodoUseCase
import com.swirlfist.desastre.domain.IObserveReminderUseCase
import com.swirlfist.desastre.domain.IObserveRemindersForTodoUseCase
import com.swirlfist.desastre.domain.IObserveTodoListUseCase
import com.swirlfist.desastre.domain.IObserveTodoUseCase
import com.swirlfist.desastre.domain.IRemoveReminderUseCase
import com.swirlfist.desastre.domain.IRemoveTodoUseCase
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
    fun provideCoroutineDispatcherProvider(): CoroutineDispatcherProvider = CoroutineDispatcherProviderImpl()

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
    fun provideReminderDao(todoDatabase: TodoDatabase): ReminderDao =
        todoDatabase.ReminderDao()

    @Singleton
    @Provides
    fun provideTodoDataSource(
        todoDao: TodoDao,
        coroutineDispatcherProvider: CoroutineDispatcherProvider,
    ): TodoDataSource = TodoDataSourceRoom(todoDao, coroutineDispatcherProvider.getIO())

    @Singleton
    @Provides
    fun provideReminderDataSource(
        reminderDao: ReminderDao,
        coroutineDispatcherProvider: CoroutineDispatcherProvider,
    ): ReminderDataSource = ReminderDataSourceRoom(reminderDao, coroutineDispatcherProvider.getIO())

    @Singleton
    @Provides
    fun provideTodoRepository(
        todoDataSource: TodoDataSource,
        reminderDataSource: ReminderDataSource,
    ): TodoRepository = TodoRepositoryImpl(todoDataSource, reminderDataSource)

    @Singleton
    @Provides
    fun provideObserveTodoUseCase(todoRepository: TodoRepository): IObserveTodoUseCase = IObserveTodoUseCase(todoRepository::observeTodo)

    @Singleton
    @Provides
    fun provideObserveTodoListUseCase(todoRepository: TodoRepository): IObserveTodoListUseCase = IObserveTodoListUseCase(todoRepository::observeTodos)

    @Singleton
    @Provides
    fun provideAddOrUpdateTodoUseCase(todoRepository: TodoRepository): IAddOrUpdateTodoUseCase = IAddOrUpdateTodoUseCase(todoRepository::addOrUpdateTodo)

    @Singleton
    @Provides
    fun provideRemoveTodoUseCase(todoRepository: TodoRepository): IRemoveTodoUseCase = IRemoveTodoUseCase(todoRepository::removeTodo)

    @Singleton
    @Provides
    fun provideObserveRemindersForTodoUseCase(todoRepository: TodoRepository): IObserveRemindersForTodoUseCase = IObserveRemindersForTodoUseCase(todoRepository::observeRemindersForTodo)

    @Singleton
    @Provides
    fun provideObserveReminderUseCase(todoRepository: TodoRepository): IObserveReminderUseCase = IObserveReminderUseCase(todoRepository::observeReminder)

    @Singleton
    @Provides
    fun provideAddOrUpdateReminderUseCase(todoRepository: TodoRepository): IAddOrUpdateReminderUseCase = IAddOrUpdateReminderUseCase(todoRepository::addOrUpdateReminder)

    @Singleton
    @Provides
    fun provideRemoveReminderUSeCase(todoRepository: TodoRepository): IRemoveReminderUseCase = IRemoveReminderUseCase(todoRepository::removeReminder)
}