package com.example.abcallapp.data.model

data class PQR(
    val title: String,
    val description: String,
    val type: String // Petición, Queja, Reclamo
)
data class PQRResponse(
    val status: String,
    val statusCode: Int,
    val ticket_number: String
)



//data class PQRItem(
//    val date: String,
//    val title: String,
//    val status: String
//)
data class PQRItem(
    val id: Int,
    val client_id: Int,
    val subject: String,
    val description: String,
    val status: String,
    val date: String,
    val estimated_close_date: String,
    val user_sub: String,
    val type: String,
    val communication_type: String
)
