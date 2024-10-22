package com.example.abcallapp.data.model

data class User(

    val username: String,
    val email: String,
    val user_role: String,
    val id: Int,
    val cognito_user_sub: String,
    val document_type: String,
    val client_id: Int,
    val id_number: String,//string
    val name: String,
    val last_name: String,
    var communication_type: String,
    val cellphone: String?
)

data class CommunicationTypeUpdate(
    val communication_type: String
)



