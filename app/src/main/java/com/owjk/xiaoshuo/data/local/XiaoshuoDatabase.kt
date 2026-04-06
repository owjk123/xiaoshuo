package com.owjk.xiaoshuo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.owjk.xiaoshuo.data.local.dao.BookDao
import com.owjk.xiaoshuo.data.local.dao.ChapterDao
import com.owjk.xiaoshuo.data.local.dao.CharacterDao
import com.owjk.xiaoshuo.data.local.dao.WorldStateDao
import com.owjk.xiaoshuo.data.local.entity.BookEntity
import com.owjk.xiaoshuo.data.local.entity.ChapterEntity
import com.owjk.xiaoshuo.data.local.entity.CharacterEntity
import com.owjk.xiaoshuo.data.local.entity.WorldStateEntity

@Database(
    entities = [
        BookEntity::class,
        ChapterEntity::class,
        CharacterEntity::class,
        WorldStateEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class XiaoshuoDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun chapterDao(): ChapterDao
    abstract fun characterDao(): CharacterDao
    abstract fun worldStateDao(): WorldStateDao
}
