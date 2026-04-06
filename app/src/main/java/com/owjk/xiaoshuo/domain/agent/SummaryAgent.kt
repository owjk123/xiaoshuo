package com.owjk.xiaoshuo.domain.agent

import com.owjk.xiaoshuo.data.preferences.AppPreferences
import com.owjk.xiaoshuo.data.remote.BaishanApiService
import javax.inject.Inject

class SummaryAgent @Inject constructor(
    apiService: BaishanApiService,
    prefs: AppPreferences
) : BaseAgent(apiService, prefs) {

    override val systemPrompt = """
你是一位专业的小说编辑助手。你的任务是将章节内容概括成简洁的摘要，用于维护故事记忆。

摘要要求：
1. 100-200字以内
2. 涵盖：主要事件、角色变化、情节推进、伏笔
3. 客观陈述，不加感情色彩
4. 直接输出摘要文字，不需要标题或说明
""".trimIndent()

    suspend fun summarize(chapterContent: String): String {
        return call("请为以下章节生成摘要：\n\n$chapterContent")
    }
}
