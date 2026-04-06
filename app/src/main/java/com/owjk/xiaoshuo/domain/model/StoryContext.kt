package com.owjk.xiaoshuo.domain.model

import com.owjk.xiaoshuo.data.local.entity.CharacterEntity
import com.owjk.xiaoshuo.data.local.entity.WorldStateEntity

data class StoryContext(
    val bookId: Long,
    val title: String,
    val genre: String,
    val synopsis: String,
    val characters: List<CharacterEntity>,
    val chapterSummaries: List<String>,
    val worldState: WorldStateEntity?,
    val nextChapterNumber: Int
) {
    fun toPromptText(): String = buildString {
        appendLine("【小说信息】")
        appendLine("书名：$title")
        appendLine("体裁：$genre")
        appendLine("简介：$synopsis")
        appendLine()

        if (worldState != null && worldState.worldRules.isNotBlank()) {
            appendLine("【世界观规则】")
            appendLine(worldState.worldRules)
            appendLine()
        }

        if (characters.isNotEmpty()) {
            appendLine("【主要角色】")
            characters.forEach { c ->
                appendLine("- ${c.name}（${c.role}）：${c.description}")
                if (c.currentState.isNotBlank()) appendLine("  当前状态：${c.currentState}")
            }
            appendLine()
        }

        if (chapterSummaries.isNotEmpty()) {
            appendLine("【前情摘要】")
            chapterSummaries.takeLast(5).forEachIndexed { i, s ->
                appendLine("第${chapterSummaries.size - chapterSummaries.takeLast(5).size + i + 1}章：$s")
            }
            appendLine()
        }

        if (worldState != null && worldState.pendingHooks.isNotBlank()) {
            appendLine("【待解决情节线索】")
            appendLine(worldState.pendingHooks)
            appendLine()
        }

        appendLine("当前需要写第 $nextChapterNumber 章。")
    }
}

sealed class PipelineEvent {
    data class Stage(val message: String) : PipelineEvent()
    data class StreamChunk(val text: String) : PipelineEvent()
    data class Error(val message: String) : PipelineEvent()
    object Done : PipelineEvent()
}

data class AuditResult(
    val passed: Boolean,
    val issues: List<String> = emptyList(),
    val suggestions: String = ""
)
