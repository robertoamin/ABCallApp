package com.example.abcallapp.network

import com.example.abcallapp.data.model.ChatGPTRequest
import com.example.abcallapp.data.model.ChatGPTResponse
import com.example.abcallapp.data.model.CohereRequest
import com.example.abcallapp.data.model.CohereResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChatGPTService {
    @POST("v1/chat/completions")
    @Headers("Content-Type: application/json")
    fun sendMessage(
        @Header("Authorization") authHeader: String,
        @Body requestBody: ChatGPTRequest
    ): Call<ChatGPTResponse>
}

interface CohereService {
    @POST("v1/generate")
    @Headers("Content-Type: application/json")
    fun generateMessage(
        @Header("Authorization") authHeader: String,
        @Body requestBody: CohereRequest
    ): Call<CohereResponse>
}
