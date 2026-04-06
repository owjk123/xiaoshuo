package com.owjk.xiaoshuo.data.local.dao

import androidx.room.*
import com.owjk.xiaoshuo.data.local.entity.CharacterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters WHERE bookId = :bookId ORDER BY name ASC")
    fun getCharactersByBook(bookId: Long): Flow<List<CharacterEntity>>

    @Query("SELECT * FROM characters WHERE bookId = :bookId")
    suspend fun getCharactersByBookSync(bookId: Long): List<CharacterEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity): Long

    @Update
    suspend fun updateCharacter(character: CharacterEntity)

    @Delete
    suspend fun deleteCharacter(character: CharacterEntity)
}
