package com.owjk.xiaoshuo.domain.agent

import com.owjk.xiaoshuo.data.preferences.AppPreferences
import com.owjk.xiaoshuo.data.remote.BaishanApiService
import com.owjk.xiaoshuo.data.remote.SseParser
import com.owjk.xiaoshuo.data.remote.dto.ChatRequest
import com.owjk.xiaoshuo.data.remote.dto.MessageDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

abstract class BaseAgent(
    private val apiService: BaishanApiService,
    private val prefs: AppPreferences
) {
    abstract val systemPrompt: String

    /**
     * 流式调用：返回 Flow<String>，每次 emit 一个文本片段
     */
    suspend fun callStream(userMessage: String): Flow<String> {
        val model = prefs.model.first()
        val request = ChatRequest(
            model = model,
            messages = listOf(
                MessageDto("system", systemPrompt),
                MessageDto("user", userMessage)
            ),
            stream = true
        )
        val body = apiService.chatCompletions(request)
        return SseParser.parseStream(body)
    }

    /**
     * 非流式调用：收集所有片段，返回完整文本
     */
    suspend fun call(userMessage: String): String {
        val sb = StringBuilder()
        callStream(userMessage).collect { sb.append(it) }
        return sb.toString()
    }
}
