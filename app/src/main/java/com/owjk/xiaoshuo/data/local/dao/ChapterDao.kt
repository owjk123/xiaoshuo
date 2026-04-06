package com.owjk.xiaoshuo.data.local.dao

import androidx.room.*
import com.owjk.xiaoshuo.data.local.entity.ChapterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapters WHERE bookId = :bookId ORDER BY chapterNumber ASC")
    fun getChaptersByBook(bookId: Long): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE bookId = :bookId ORDER BY chapterNumber DESC LIMIT 1")
    suspend fun getLatestChapter(bookId: Long): ChapterEntity?

    @Query("SELECT * FROM chapters WHERE id = :id")
    suspend fun getChapterById(id: Long): ChapterEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: ChapterEntity): Long

    @Update
    suspend fun updateChapter(chapter: ChapterEntity)

    @Delete
    suspend fun deleteChapter(chapter: ChapterEntity)

    @Query("SELECT COUNT(*) FROM chapters WHERE bookId = :bookId")
    suspend fun countByBook(bookId: Long): Int

    @Query("SELECT SUM(wordCount) FROM chapters WHERE bookId = :bookId")
    suspend fun totalWordsByBook(bookId: Long): Int?

    @Query("SELECT summary FROM chapters WHERE bookId = :bookId ORDER BY chapterNumber ASC")
    suspend fun getAllSummaries(bookId: Long): List<String>
}
