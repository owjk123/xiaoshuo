package com.owjk.xiaoshuo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "world_state",
    foreignKeys = [ForeignKey(
        entity = BookEntity::class,
        parentColumns = ["id"],
        childColumns = ["bookId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("bookId")]
)
data class WorldStateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bookId: Long,
    val currentLocation: String = "",
    val timelinePosition: String = "",
    val worldRules: String = "",       // 世界观规则（修炼体系、魔法等）
    val pendingHooks: String = "",     // 未解决的情节线索（JSON）
    val subplots: String = "",         // 子情节（JSON）
    val emotionalArcs: String = "",    // 情感弧线（JSON）
    val resourceLedger: String = "",   // 资源账本（JSON）
    val updatedAt: Long = System.currentTimeMillis()
)
