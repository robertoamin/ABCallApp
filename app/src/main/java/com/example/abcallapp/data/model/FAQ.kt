package com.example.abcallapp.data.model

// Clase para almacenar preguntas frecuentes
data class FAQ(
    val title: String,
    val content: String,
    val tags: List<tag>
)

data class tag(
    val id:Int,
    val name: String,
    val client_id:Int
)

data class KnowledgeBaseRequest(
    val title: String,
    val content: String = "",
    val tags: List<Int>
)
