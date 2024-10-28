package com.example.abcallapp.data.model

data class Client(
    val id: Int,
    val legal_name: String

)
data class ClientResponse(
    val status: String,
    val data: List<Client>
)