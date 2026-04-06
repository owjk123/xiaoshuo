package com.owjk.xiaoshuo.di

import android.content.Context
import androidx.room.Room
import com.owjk.xiaoshuo.data.local.XiaoshuoDatabase
import com.owjk.xiaoshuo.data.local.dao.BookDao
import com.owjk.xiaoshuo.data.local.dao.ChapterDao
import com.owjk.xiaoshuo.data.local.dao.CharacterDao
import com.owjk.xiaoshuo.data.local.dao.WorldStateDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): XiaoshuoDatabase =
        Room.databaseBuilder(context, XiaoshuoDatabase::class.java, "xiaoshuo.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideBookDao(db: XiaoshuoDatabase): BookDao = db.bookDao()
    @Provides fun provideChapterDao(db: XiaoshuoDatabase): ChapterDao = db.chapterDao()
    @Provides fun provideCharacterDao(db: XiaoshuoDatabase): CharacterDao = db.characterDao()
    @Provides fun provideWorldStateDao(db: XiaoshuoDatabase): WorldStateDao = db.worldStateDao()
}
