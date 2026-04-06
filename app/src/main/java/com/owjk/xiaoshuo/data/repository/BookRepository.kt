package com.owjk.xiaoshuo.data.repository

import com.owjk.xiaoshuo.data.local.dao.BookDao
import com.owjk.xiaoshuo.data.local.dao.ChapterDao
import com.owjk.xiaoshuo.data.local.dao.CharacterDao
import com.owjk.xiaoshuo.data.local.dao.WorldStateDao
import com.owjk.xiaoshuo.data.local.entity.BookEntity
import com.owjk.xiaoshuo.data.local.entity.ChapterEntity
import com.owjk.xiaoshuo.data.local.entity.CharacterEntity
import com.owjk.xiaoshuo.data.local.entity.WorldStateEntity
import com.owjk.xiaoshuo.domain.model.StoryContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(
    private val bookDao: BookDao,
    private val chapterDao: ChapterDao,
    private val characterDao: CharacterDao,
    private val worldStateDao: WorldStateDao
) {
    fun getAllBooks(): Flow<List<BookEntity>> = bookDao.getAllBooks()

    suspend fun getBookById(id: Long): BookEntity? = bookDao.getBookById(id)

    suspend fun createBook(title: String, genre: String, synopsis: String): Long {
        val book = BookEntity(title = title, genre = genre, synopsis = synopsis)
        val bookId = bookDao.insertBook(book)
        // init empty world state
        worldStateDao.insertWorldState(WorldStateEntity(bookId = bookId))
        return bookId
    }

    suspend fun updateBook(book: BookEntity) = bookDao.updateBook(book)

    suspend fun deleteBook(book: BookEntity) = bookDao.deleteBook(book)

    fun getChaptersByBook(bookId: Long): Flow<List<ChapterEntity>> =
        chapterDao.getChaptersByBook(bookId)

    suspend fun getLatestChapter(bookId: Long): ChapterEntity? =
        chapterDao.getLatestChapter(bookId)

    suspend fun saveChapter(bookId: Long, chapterNumber: Int, title: String, content: String, summary: String): Long {
        val wordCount = content.length
        val chapter = ChapterEntity(
            bookId = bookId,
            chapterNumber = chapterNumber,
            title = title,
            content = content,
            summary = summary,
            wordCount = wordCount,
            updatedAt = System.currentTimeMillis()
        )
        val chapterId = chapterDao.insertChapter(chapter)

        // update book stats
        val book = bookDao.getBookById(bookId)
        if (book != null) {
            val totalWords = chapterDao.totalWordsByBook(bookId) ?: 0
            val count = chapterDao.countByBook(bookId)
            bookDao.updateBook(book.copy(chapterCount = count, totalWords = totalWords, updatedAt = System.currentTimeMillis()))
        }
        return chapterId
    }

    suspend fun updateChapterContent(chapterId: Long, content: String) {
        val chapter = chapterDao.getChapterById(chapterId) ?: return
        chapterDao.updateChapter(chapter.copy(content = content, wordCount = content.length, updatedAt = System.currentTimeMillis()))
    }

    fun getCharactersByBook(bookId: Long): Flow<List<CharacterEntity>> =
        characterDao.getCharactersByBook(bookId)

    suspend fun saveCharacter(character: CharacterEntity): Long =
        characterDao.insertCharacter(character)

    suspend fun updateCharacter(character: CharacterEntity) =
        characterDao.updateCharacter(character)

    suspend fun deleteCharacter(character: CharacterEntity) =
        characterDao.deleteCharacter(character)

    suspend fun getWorldState(bookId: Long): WorldStateEntity? =
        worldStateDao.getWorldState(bookId)

    suspend fun updateWorldState(state: WorldStateEntity) =
        worldStateDao.updateWorldState(state)

    suspend fun getStoryContext(bookId: Long): StoryContext {
        val book = bookDao.getBookById(bookId) ?: error("Book not found")
        val characters = characterDao.getCharactersByBookSync(bookId)
        val summaries = chapterDao.getAllSummaries(bookId)
        val worldState = worldStateDao.getWorldState(bookId)
        val latestChapter = chapterDao.getLatestChapter(bookId)

        return StoryContext(
            bookId = bookId,
            title = book.title,
            genre = book.genre,
            synopsis = book.synopsis,
            characters = characters,
            chapterSummaries = summaries,
            worldState = worldState,
            nextChapterNumber = (latestChapter?.chapterNumber ?: 0) + 1
        )
    }
}
