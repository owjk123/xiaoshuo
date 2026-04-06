package com.owjk.xiaoshuo.domain.agent

import com.owjk.xiaoshuo.data.preferences.AppPreferences
import com.owjk.xiaoshuo.data.remote.BaishanApiService
import javax.inject.Inject

class WriterAgent @Inject constructor(
    apiService: BaishanApiService,
    prefs: AppPreferences
) : BaseAgent(apiService, prefs) {

    override val systemPrompt = """
你是一位才华横溢的网络小说作家。你的任务是根据大纲写出精彩的章节正文。

写作要求：
1. 字数在2000-4000字之间，内容饱满
2. 多用细节描写，展现场景氛围和人物情感
3. 对话生动自然，符合各角色性格
4. 节奏把控得当，张弛有度
5. 适当使用比喻、排比等修辞手法增强表达
6. 避免使用过于书面化或AI感强的表达
7. 结尾留有悬念，吸引读者继续阅读

输出格式：
直接输出章节正文，以章节标题"第X章 标题"开头。
不要有任何额外说明或注释。
""".trimIndent()
}
