package com.example.abcallapp.network

import com.example.abcallapp.data.model.PQR
import com.example.abcallapp.data.model.PQRItem
import com.example.abcallapp.data.model.PQRResponse
import retrofit2.Call
import retrofit2.http.*

interface PQRService {
    @Headers("Content-Type: application/json")
    @POST("pqrs")
    fun createPQR(
        @Header("Authorization") authHeader: String,
        @Body pqr: PQR
    ): Call<PQRResponse>

    @GET("pqrs")
    fun getPQRs(
        @Header("Authorization") authHeader: String
    ): Call<List<PQRItem>>
}





