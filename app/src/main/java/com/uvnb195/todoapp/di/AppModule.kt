package com.uvnb195.todoapp.di

import android.content.Context
import androidx.room.Room
import com.uvnb195.todoapp.data.TodoDatabase
import com.uvnb195.todoapp.data.TodoRepository
import com.uvnb195.todoapp.data.TodoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideTodoDatabase(@ApplicationContext appContext: Context): TodoDatabase {
        return Room.databaseBuilder(
            appContext,
            TodoDatabase::class.java,
            "todo_db"
        )

            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideTodoRepository(db: TodoDatabase): TodoRepository {
        return TodoRepositoryImpl(db.dao)
    }
}