package com.owjk.xiaoshuo.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatRequest(
    @Json(name = "model") val model: String,
    @Json(name = "messages") val messages: List<MessageDto>,
    @Json(name = "stream") val stream: Boolean = true,
    @Json(name = "temperature") val temperature: Double = 0.8,
    @Json(name = "max_tokens") val maxTokens: Int = 4096
)

@JsonClass(generateAdapter = true)
data class MessageDto(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class ChatResponse(
    @Json(name = "id") val id: String,
    @Json(name = "choices") val choices: List<Choice>
)

@JsonClass(generateAdapter = true)
data class Choice(
    @Json(name = "delta") val delta: Delta?,
    @Json(name = "message") val message: MessageDto?,
    @Json(name = "finish_reason") val finishReason: String?
)

@JsonClass(generateAdapter = true)
data class Delta(
    @Json(name = "role") val role: String?,
    @Json(name = "content") val content: String?
)

@JsonClass(generateAdapter = true)
data class ModelsResponse(
    @Json(name = "data") val data: List<ModelInfo>
)

@JsonClass(generateAdapter = true)
data class ModelInfo(
    @Json(name = "id") val id: String,
    @Json(name = "object") val objectType: String
)
