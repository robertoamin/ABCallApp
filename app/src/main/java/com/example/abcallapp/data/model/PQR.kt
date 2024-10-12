package com.example.abcallapp.data.model

data class PQR(
    val subject: String,
    val description: String,
    val type: String // Petición, Queja, Reclamo
)
data class PQRResponse(
    val message: String,
    val statusCode: Int
)


data class PQRItem(
    val date: String,
    val title: String,
    val status: String
)
