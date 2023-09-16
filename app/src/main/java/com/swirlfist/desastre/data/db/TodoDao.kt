package com.swirlfist.desastre.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.swirlfist.desastre.data.model.Todo
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert
    fun insert(todo: Todo): Long

    @Delete
    fun delete(todo: Todo)

    @Query("DELETE FROM todos WHERE id=:todoId")
    fun delete(todoId: Long)

    @Query("SELECT * FROM todos")
    fun observeAll(): Flow<List<Todo>>

    @Query("SELECT * FROM todos WHERE id=:todoId")
    fun observeTodo(todoId: Long): Flow<Todo?>
}