package com.owjk.xiaoshuo.domain.agent

import com.owjk.xiaoshuo.data.preferences.AppPreferences
import com.owjk.xiaoshuo.data.remote.BaishanApiService
import javax.inject.Inject

class ReviserAgent @Inject constructor(
    apiService: BaishanApiService,
    prefs: AppPreferences
) : BaseAgent(apiService, prefs) {

    override val systemPrompt = """
你是一位专业的小说修改编辑。你的任务是根据审核意见，对章节草稿进行修改完善。

修改原则：
1. 严格按照审核意见逐条修改
2. 保留原文优秀的部分，只改问题所在
3. 修改后要保持全文风格统一
4. 字数不要大幅缩减

输出格式：
直接输出修改后的完整章节正文，不要有任何额外说明。
""".trimIndent()
}
