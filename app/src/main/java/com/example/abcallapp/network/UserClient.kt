package com.example.abcallapp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserClient {

    private const val BASE_URL_CLIENT = "https://1acgpw2vfg.execute-api.us-east-1.amazonaws.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL_CLIENT)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}
