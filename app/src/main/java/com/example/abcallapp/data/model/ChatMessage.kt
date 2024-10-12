package com.example.abcallapp.data.model

data class ChatMessage(
    val text: String,
    val isUser: Boolean // Este campo indicará si el mensaje es del usuario o del bot
)

