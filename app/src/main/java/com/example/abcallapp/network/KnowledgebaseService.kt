package com.example.abcallapp.network


import com.example.abcallapp.data.model.FAQ
import com.example.abcallapp.data.model.KnowledgeBaseRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.http.Header

interface KnowledgebaseService {
    @POST("api/knowledgebase/filters")
    fun getKnowledgeBaseAssigned(
        @Header("Authorization") authHeader: String,
        @Body body: KnowledgeBaseRequest
    ): Call<List<FAQ>>
}

