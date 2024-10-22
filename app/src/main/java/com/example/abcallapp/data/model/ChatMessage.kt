package com.example.abcallapp.data.model


data class ChatMessage(
    val text: String,
    val isUser: Boolean // Este campo indicará si el mensaje es del usuario o del bot
)

data class CohereRequest(
    val model: String = "command-xlarge-nightly", // Modelo gratuito de Cohere LLM
    val prompt: String, // El mensaje del usuario
    val max_tokens: Int = 50, // Límite de tokens
    val temperature: Double = 0.5 // Parámetro de aleatoriedad de la respuesta
)

data class CohereResponse(
    val generations: List<CohereGeneration>
)

data class CohereGeneration(
    val text: String
)


data class ChatGPTRequest(
    val model: String,
    val messages: List<ChatGPTMessage>,
    val max_tokens: Int
)

data class ChatGPTMessage(
    val role: String,  // "user" para el usuario, "system" para el bot
    val content: String
)

data class ChatGPTResponse(
    val choices: List<ChatGPTChoice>
)

data class ChatGPTChoice(
    val message: ChatGPTMessage
)
