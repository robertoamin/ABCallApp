package com.example.abcallapp.data.model

data class PQR(
    val title: String,
    val description: String,
    val type: String // Petición, Queja, Reclamo
)
data class PQRResponse(
    val status: String,
    val statusCode: Int
)



//data class PQRItem(
//    val date: String,
//    val title: String,
//    val status: String
//)
data class PQRItem(
    val client_id: String,
    val subject: String,
    val description: String,
    val status: String,
    val date: String,
    val estimated_close_date: String,
    val user_id: String,
    val type: String
)

