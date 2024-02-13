package com.swirlfist.desastre

import android.content.Context
import androidx.room.Room
import com.swirlfist.desastre.data.CoroutineDispatcherProvider
import com.swirlfist.desastre.data.ICoroutineDispatcherProvider
import com.swirlfist.desastre.data.ITodoRepository
import com.swirlfist.desastre.data.TodoRepository
import com.swirlfist.desastre.data.db.ReminderDao
import com.swirlfist.desastre.data.db.TodoDao
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
    fun provideCoroutineDispatcherProvider(): ICoroutineDispatcherProvider = CoroutineDispatcherProvider()

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
    fun provideTodoRepository(
        todoDao: TodoDao,
        reminderDao: ReminderDao,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): ITodoRepository {
        return TodoRepository(todoDao, reminderDao, coroutineDispatcherProvider.getIO())
    }

    @Singleton
    @Provides
    fun provideObserveTodoUseCase(todoRepository: ITodoRepository): IObserveTodoUseCase = IObserveTodoUseCase(todoRepository::observeTodo)

    @Singleton
    @Provides
    fun provideObserveTodoListUseCase(todoRepository: ITodoRepository): IObserveTodoListUseCase = IObserveTodoListUseCase(todoRepository::observeTodos)

    @Singleton
    @Provides
    fun provideAddOrUpdateTodoUseCase(todoRepository: ITodoRepository): IAddOrUpdateTodoUseCase = IAddOrUpdateTodoUseCase(todoRepository::addOrUpdateTodo)

    @Singleton
    @Provides
    fun provideRemoveTodoUseCase(todoRepository: ITodoRepository): IRemoveTodoUseCase = IRemoveTodoUseCase(todoRepository::removeTodo)

    @Singleton
    @Provides
    fun provideObserveRemindersForTodoUseCase(todoRepository: ITodoRepository): IObserveRemindersForTodoUseCase = IObserveRemindersForTodoUseCase(todoRepository::observeRemindersForTodo)

    @Singleton
    @Provides
    fun provideObserveReminderUseCase(todoRepository: ITodoRepository): IObserveReminderUseCase = IObserveReminderUseCase(todoRepository::observeReminder)

    @Singleton
    @Provides
    fun provideAddOrUpdateReminderUseCase(todoRepository: ITodoRepository): IAddOrUpdateReminderUseCase = IAddOrUpdateReminderUseCase(todoRepository::addOrUpdateReminder)

    @Singleton
    @Provides
    fun provideRemoveReminderUSeCase(todoRepository: ITodoRepository): IRemoveReminderUseCase = IRemoveReminderUseCase(todoRepository::removeReminder)
}