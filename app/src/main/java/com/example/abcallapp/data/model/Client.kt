package com.example.abcallapp.data.model

data class Client(
    val id: Int,
    val perfil: String,
    val id_type: String,
    val legal_name: String,
    val id_number: Int,
    val address: String,
    val type_document_rep: String,
    val id_rep_lega: Int,
    val name_rep: String,
    val last_name_rep: String,
    val email_rep: String,
    val plan_type: String
)
data class ClientResponse(
    val status: String,
    val data: Client
)