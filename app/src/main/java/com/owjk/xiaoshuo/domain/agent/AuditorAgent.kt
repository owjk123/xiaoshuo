package com.owjk.xiaoshuo.domain.agent

import com.owjk.xiaoshuo.data.preferences.AppPreferences
import com.owjk.xiaoshuo.data.remote.BaishanApiService
import com.owjk.xiaoshuo.domain.model.AuditResult
import javax.inject.Inject

class AuditorAgent @Inject constructor(
    apiService: BaishanApiService,
    prefs: AppPreferences
) : BaseAgent(apiService, prefs) {

    override val systemPrompt = """
你是一位严格的小说编辑和审稿人。你的任务是检查章节草稿是否存在问题。

检查维度：
1. 人物一致性：角色行为是否符合其性格设定和当前状态
2. 情节连续性：是否与前情摘要衔接自然
3. 世界观一致性：是否遵守世界观规则（如修炼体系、地理设定等）
4. 逻辑合理性：事件发展是否合乎逻辑
5. 伏笔呼应：是否有未解决的伏笔需要推进

输出格式（严格遵守）：
PASS 或 FAIL

问题列表（如有）：
- [人物] 问题描述
- [情节] 问题描述

修改建议：
具体建议文字
""".trimIndent()

    suspend fun audit(draft: String, contextText: String): AuditResult {
        val prompt = "【故事背景】\n$contextText\n\n【本章草稿】\n$draft\n\n请对本章草稿进行审核。"
        val result = call(prompt)
        val passed = result.trimStart().startsWith("PASS")
        val issues = extractIssues(result)
        return AuditResult(passed = passed, issues = issues, suggestions = result)
    }

    private fun extractIssues(text: String): List<String> {
        return text.lines()
            .filter { it.trimStart().startsWith("- ") }
            .map { it.trim().removePrefix("- ") }
    }
}
