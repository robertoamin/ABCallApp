package com.example.abcallapp.network


import com.example.abcallapp.data.model.Client
import com.example.abcallapp.data.model.ClientResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header


interface ClientService {
    @GET("api/clients/short")
    fun getClientDetails(@Header("Authorization") authHeader: String): Call<List<Client>>
}
