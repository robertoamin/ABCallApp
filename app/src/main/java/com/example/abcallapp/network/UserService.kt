package com.example.abcallapp.network

import com.example.abcallapp.data.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PUT


interface UserService {
    @GET("api/user/me")
    fun getUserDetails(@Header("Authorization") authHeader: String): Call<User>

    @PUT("api/user/me")
    fun editUserCommunication(
        @Header("Authorization") authHeader: String,
        @Body updatedUser: User  // Cambiamos el cuerpo a un objeto User
    ): Call<User>
}





