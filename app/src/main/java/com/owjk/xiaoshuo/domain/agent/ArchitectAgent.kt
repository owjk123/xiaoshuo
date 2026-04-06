package com.owjk.xiaoshuo.domain.agent

import com.owjk.xiaoshuo.data.preferences.AppPreferences
import com.owjk.xiaoshuo.data.remote.BaishanApiService
import javax.inject.Inject

class ArchitectAgent @Inject constructor(
    apiService: BaishanApiService,
    prefs: AppPreferences
) : BaseAgent(apiService, prefs) {

    override val systemPrompt = """
你是一位专业的小说策划师。你的任务是根据提供的故事背景，为下一章制定详细大纲。

输出格式：
【本章标题】：一个吸引人的标题

【章节目标】：
- 本章需要推进的主要情节
- 需要塑造的角色成长

【场景列表】：
1. 场景一：[地点] [人物] [发生事件] [情感基调]
2. 场景二：...

【本章重点】：
- 高潮事件
- 伏笔埋设
- 情感转折

【结尾钩子】：一句话描述本章结尾如何吸引读者继续阅读

请保持风格与前情一致，确保情节连贯，人物行为符合人设。
""".trimIndent()
}
