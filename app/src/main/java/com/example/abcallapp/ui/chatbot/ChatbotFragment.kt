package com.example.abcallapp.ui.chatbot

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abcallapp.R
import com.example.abcallapp.adapters.ChatbotAdapter
import com.example.abcallapp.data.model.*
import com.example.abcallapp.databinding.FragmentChatbotBinding
import com.example.abcallapp.network.ChatGPTClient
import com.example.abcallapp.network.ChatGPTService
import com.example.abcallapp.network.CohereClient
import com.example.abcallapp.network.CohereService
import com.example.abcallapp.ui.notifications.NotificationManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ChatbotFragment : Fragment() {

    private var _binding: FragmentChatbotBinding? = null
    private val binding get() = _binding!!

    // Lista de mensajes de la conversación
    private val displayedMessages = mutableListOf<ChatMessage>() // Mensajes mostrados
    private lateinit var adapter: ChatbotAdapter

    // Variable de control para la primera respuesta del bot
    private var isFirstBotResponse = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatbotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener la fecha y hora actuales
        updateDateTime()

        // Configurar el RecyclerView con el adaptador
        adapter = ChatbotAdapter(displayedMessages)
        binding.chatbotRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatbotRecyclerView.adapter = adapter

        // Mostrar el mensaje de bienvenida del bot al iniciar
        displayedMessages.add(ChatMessage(getString(R.string.Bot_welcome), false))
        adapter.notifyItemInserted(displayedMessages.size - 1)
        binding.chatbotRecyclerView.scrollToPosition(displayedMessages.size - 1)

        // Manejar el evento de clic del botón "Enviar"
        binding.sendButton.setOnClickListener {
            val userInput = binding.chatBotEditText.text.toString().trim()
            if (userInput.isNotEmpty()) {
                // Añadir el mensaje del usuario a la conversación
                addMessage(userInput, true)

                // Limpiar el campo de entrada
                binding.chatBotEditText.text.clear()

                // Simular respuesta del bot
                //simulateBotResponse(userMessage)
            }
        }

// Escuchar cuando se presiona Enter en la caja de texto
        binding.chatBotEditText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                handleSendMessage() // Al presionar Enter, enviar el mensaje
                true
            } else {
                false
            }
        }


        // Acción del botón "Cancelar"
        //binding.cancelButton.setOnClickListener {
        //    findNavController().navigate(R.id.navigation_home)
        //}
    }
    private fun updateDateTime() {
        val timeZone = TimeZone.getTimeZone("America/Bogota")
        val currentDate = Calendar.getInstance(timeZone).time
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        // Aplicar la zona horaria correcta
        dateFormat.timeZone = timeZone
        timeFormat.timeZone = timeZone

        binding.chatbotDate.text = dateFormat.format(currentDate) // Actualiza la fecha
        binding.chatbotTime.text = timeFormat.format(currentDate) // Actualiza la hora
    }

    private fun handleSendMessage() {
        val userMessage = binding.chatBotEditText.text.toString()

        if (userMessage.isNotEmpty()) {
            // Agregar el mensaje del usuario a la lista
            displayedMessages.add(ChatMessage(userMessage, true))
            adapter.notifyItemInserted(displayedMessages.size - 1)
            binding.chatbotRecyclerView.scrollToPosition(displayedMessages.size - 1)

            // Vaciar la caja de texto y reiniciar el hint
            binding.chatBotEditText.text.clear()
            binding.chatBotEditText.hint = "" // Eliminar el hint

            // respuesta del bot (mensaje chatgpt)
            simulateBotResponse(userMessage)
            //simulateBotResponse()

        }
    }

    // Función para agregar un mensaje a la lista
    private fun addMessage(text: String, isUser: Boolean) {
        displayedMessages.add(ChatMessage(text, isUser))
        adapter.notifyItemInserted(displayedMessages.size - 1)
        binding.chatbotRecyclerView.scrollToPosition(displayedMessages.size - 1) // Scroll hasta el final
    }

    // Función para simular una respuesta del bot
   // private fun simulateBotResponse() {
   //     Handler(Looper.getMainLooper()).postDelayed({
            // Añadir una respuesta simple del bot
   //         addMessage(getString(R.string.Bot_response), false)
   //     }, 1000) // Simula un retraso de 1 segundo
    //}

    private fun simulateBotResponse(userInput: String) {
        getBotResponse(userInput)  // Llama a la función que hace la solicitud a ChatGPT
    }


    private fun getBotResponse(userInput: String) {

        //val service = ChatGPTClient.retrofit.create(ChatGPTService::class.java)   acceso a servicio de chatGPT
        val service = CohereClient.retrofit.create(CohereService::class.java)

        val apiKey = "Bearer 4FBgLHiMJTiHygmvXQ7S8Dg967i3Ludqg4xc5tFy"
        val messages = listOf(
            ChatGPTMessage("system", "You are a customer service chatbot specialized in complaints, claims, and petitions."),
            ChatGPTMessage("user", userInput)
        )

        //val requestBody = ChatGPTRequest(
        //    model = "gpt-3.5-turbo",
        //    messages = messages,
        //    max_tokens = 50  // Limitar el número de tokens
        //)
        val requestBody = CohereRequest(
            prompt = userInput
        )
        // Solo generar la notificación en la primera respuesta del bot
        if (isFirstBotResponse) {
            NotificationManager.addNotification("Conversación con Bot iniciada")
            isFirstBotResponse = false // Cambiar el estado para que no se repita
        }

        //val call = service.sendMessage(apiKey, requestBody)  //modelo chatgpt
        val call = service.generateMessage(apiKey, requestBody) //modelo cohere

        //call.enqueue(object : Callback<ChatGPTResponse> {
        //    override fun onResponse(call: Call<ChatGPTResponse>, response: Response<ChatGPTResponse>) {
        //        if (response.isSuccessful && response.body() != null) {
        //            val botReply = response.body()!!.choices.first().message.content
        //            val truncatedReply = botReply.split(" ").take(10).joinToString(" ")  // Limita la respuesta a 10 palabras

//                    addMessage(truncatedReply, false)  // Muestra el mensaje del bot
//                } else {
//                    addMessage("Error: No se pudo obtener la respuesta del bot.", false)
//                }
//            }
        // Modelo Cohere
        call.enqueue(object : Callback<CohereResponse> {
            override fun onResponse(call: Call<CohereResponse>, response: Response<CohereResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val botReply = response.body()!!.generations.first().text
                    val truncatedReply = botReply.split(" ").take(20).joinToString(" ") // Limita la respuesta a 10 palabras

                    addMessage(truncatedReply, false)  // Muestra el mensaje del bot
                } else {
                    addMessage("Error: No se pudo obtener la respuesta del bot.", false)
                }
            }

            override fun onFailure(call: Call<CohereResponse>, t: Throwable) {
                addMessage("Error: ${t.message}", false)  // Muestra un error si la solicitud falla
            }
        })


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



