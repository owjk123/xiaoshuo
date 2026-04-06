package com.owjk.xiaoshuo.data.remote

import com.owjk.xiaoshuo.data.remote.dto.ChatRequest
import com.owjk.xiaoshuo.data.remote.dto.ModelsResponse
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Streaming

interface BaishanApiService {

    @POST("chat/completions")
    @Streaming
    suspend fun chatCompletions(
        @Body request: ChatRequest
    ): ResponseBody

    @GET("models")
    suspend fun listModels(): ModelsResponse
}
