package com.owjk.xiaoshuo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "characters",
    foreignKeys = [ForeignKey(
        entity = BookEntity::class,
        parentColumns = ["id"],
        childColumns = ["bookId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("bookId")]
)
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Long,
    val name: String,
    val role: String = "",        // 主角/配角/反派 etc.
    val description: String = "", // 外貌、性格
    val background: String = "",  // 背景故事
    val currentState: String = "",// 当前状态
    val relationships: String = "" // 人物关系（JSON格式）
)
