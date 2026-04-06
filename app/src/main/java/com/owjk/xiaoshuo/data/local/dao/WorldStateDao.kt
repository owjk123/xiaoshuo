package com.owjk.xiaoshuo.data.local.dao

import androidx.room.*
import com.owjk.xiaoshuo.data.local.entity.WorldStateEntity

@Dao
interface WorldStateDao {
    @Query("SELECT * FROM world_state WHERE bookId = :bookId LIMIT 1")
    suspend fun getWorldState(bookId: Long): WorldStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorldState(state: WorldStateEntity): Long

    @Update
    suspend fun updateWorldState(state: WorldStateEntity)
}
