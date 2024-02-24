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
import com.swirlfist.desastre.domain.AddOrUpdateReminderUseCase
import com.swirlfist.desastre.domain.AddOrUpdateTodoUseCase
import com.swirlfist.desastre.domain.ObserveReminderUseCase
import com.swirlfist.desastre.domain.ObserveRemindersForTodoUseCase
import com.swirlfist.desastre.domain.ObserveTodoListUseCase
import com.swirlfist.desastre.domain.ObserveTodoUseCase
import com.swirlfist.desastre.domain.RemoveReminderUseCase
import com.swirlfist.desastre.domain.RemoveTodoUseCase
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
    fun provideObserveTodoUseCase(todoRepository: TodoRepository): ObserveTodoUseCase = ObserveTodoUseCase(todoRepository::observeTodo)

    @Singleton
    @Provides
    fun provideObserveTodoListUseCase(todoRepository: TodoRepository): ObserveTodoListUseCase = ObserveTodoListUseCase(todoRepository::observeTodos)

    @Singleton
    @Provides
    fun provideAddOrUpdateTodoUseCase(todoRepository: TodoRepository): AddOrUpdateTodoUseCase = AddOrUpdateTodoUseCase(todoRepository::addOrUpdateTodo)

    @Singleton
    @Provides
    fun provideRemoveTodoUseCase(todoRepository: TodoRepository): RemoveTodoUseCase = RemoveTodoUseCase(todoRepository::removeTodo)

    @Singleton
    @Provides
    fun provideObserveRemindersForTodoUseCase(todoRepository: TodoRepository): ObserveRemindersForTodoUseCase = ObserveRemindersForTodoUseCase(todoRepository::observeRemindersForTodo)

    @Singleton
    @Provides
    fun provideObserveReminderUseCase(todoRepository: TodoRepository): ObserveReminderUseCase = ObserveReminderUseCase(todoRepository::observeReminder)

    @Singleton
    @Provides
    fun provideAddOrUpdateReminderUseCase(todoRepository: TodoRepository): AddOrUpdateReminderUseCase = AddOrUpdateReminderUseCase(todoRepository::addOrUpdateReminder)

    @Singleton
    @Provides
    fun provideRemoveReminderUSeCase(todoRepository: TodoRepository): RemoveReminderUseCase = RemoveReminderUseCase(todoRepository::removeReminder)
}