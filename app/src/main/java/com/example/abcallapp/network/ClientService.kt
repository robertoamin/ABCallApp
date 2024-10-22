package com.example.abcallapp.network


import com.example.abcallapp.data.model.Client
import com.example.abcallapp.data.model.ClientResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header


interface ClientService {
    @GET("api/client/my")
    fun getClientDetails(@Header("Authorization") authHeader: String): Call<ClientResponse>
}
