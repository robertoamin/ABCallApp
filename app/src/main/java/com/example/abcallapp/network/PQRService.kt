package com.example.abcallapp.network

import com.example.abcallapp.data.model.PQR
import com.example.abcallapp.data.model.PQRResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PQRService {
    @POST("incidents")
    fun createPQR(@Body pqr: PQR): Call<PQRResponse>
}
