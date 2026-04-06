package com.owjk.xiaoshuo.data.remote

import com.owjk.xiaoshuo.data.remote.dto.ChatResponse
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody

object SseParser {

    private val moshi = Moshi.Builder().build()
    private val adapter = moshi.adapter(ChatResponse::class.java)

    fun parseStream(body: ResponseBody): Flow<String> = flow {
        body.source().use { source ->
            while (!source.exhausted()) {
                val line = source.readUtf8Line() ?: break
                if (line.startsWith("data: ")) {
                    val data = line.removePrefix("data: ").trim()
                    if (data == "[DONE]") break
                    try {
                        val response = adapter.fromJson(data)
                        val content = response?.choices?.firstOrNull()?.delta?.content
                        if (!content.isNullOrEmpty()) {
                            emit(content)
                        }
                    } catch (_: Exception) {
                        // skip malformed lines
                    }
                }
            }
        }
    }.flowOn(Dispatchers.IO)
}
