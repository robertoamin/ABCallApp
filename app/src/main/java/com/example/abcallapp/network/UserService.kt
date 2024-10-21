package com.example.abcallapp.network

import com.example.abcallapp.data.model.User
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header


interface UserService {
    @GET("api/user/me")
    fun getUserDetails(@Header("Authorization") authHeader: String): Call<User>
}



