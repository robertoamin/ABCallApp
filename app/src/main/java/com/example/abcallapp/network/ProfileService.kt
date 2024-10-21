package com.example.abcallapp.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.PUT

interface ProfileService {
    @PUT("/api/perfil") // Ruta ejemplo del PUT en el microservicio
    fun actualizarPerfil(@Body perfilModificado: Map<String, String>): Call<Void>
}
