package com.example.abcallapp.ui.chatbot

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abcallapp.R
import com.example.abcallapp.adapters.ChatbotAdapter
import com.example.abcallapp.data.model.*
import com.example.abcallapp.databinding.FragmentChatbotBinding
import com.example.abcallapp.network.*
import com.example.abcallapp.ui.notifications.NotificationManager
import com.example.abcallapp.utils.UserPreferences
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
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
    private var knowledgeBaseContext: String? = null

    // Contador para rastrear el número de interacciones del usuario
    private var userMessageCount = 0
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

        // Cargar el contexto de la base de conocimiento
        loadKnowledgeBaseContext()

        // Manejar el evento de clic del botón "Enviar"
        binding.sendButton.setOnClickListener {
            val userInput = binding.chatBotEditText.text.toString().trim()
            if (userInput.isNotEmpty()) {
                userMessageCount++
                // Añadir el mensaje del usuario a la conversación
                addMessage(userInput, true)
                // Limpiar el campo de entrada
                binding.chatBotEditText.text.clear()

                // Simular respuesta del bot
                //simulateBotResponse(userMessage)
                // Verifica si el mensaje contiene palabras clave o si es la sexta interacción
                if (shouldShowPQRSuggestion(userInput)) {
                    showPQRSuggestion()
                } else {
                    simulateBotResponse(userInput)
                }
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
    private fun shouldShowPQRSuggestion(userInput: String): Boolean {
        val keywords = listOf("PQR", "pqr", "Pqr", "poner un PQR", "dónde pongo un PQR")
        val containsKeyword = keywords.any { keyword -> userInput.contains(keyword, ignoreCase = true) }
        return containsKeyword || userMessageCount == 6
    }
    private fun showPQRSuggestion() {
        val linkText = getString(R.string.pqr_link_text)
        val messageText = getString(R.string.pqr_message, linkText)
        val spannableMessage = SpannableString(messageText)
        val startIndex = spannableMessage.indexOf(linkText)
        val endIndex = startIndex + linkText.length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.navigation_create_pqr) // Navegar al fragmento de creación de PQR
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = ContextCompat.getColor(requireContext(), R.color.white) // Cambia el color del enlace
                ds.isUnderlineText = true // Subrayar el enlace
            }
        }

        spannableMessage.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Agregar el mensaje con SpannableString a displayedMessages
        displayedMessages.add(ChatMessage(spannableMessage, false))
        adapter.notifyItemInserted(displayedMessages.size - 1)
        binding.chatbotRecyclerView.scrollToPosition(displayedMessages.size - 1)
    }

    private fun loadKnowledgeBaseContext() {
        // Obtener el idToken de SharedPreferences
        val userPrefs = UserPreferences.getInstance(requireContext())
        val idToken = userPrefs.getIdToken()

        if (idToken.isNullOrEmpty()) {
            Log.e("ChatbotFragment", "No se encontró un idToken válido.")
            return
        }

        val authHeader = "Bearer $idToken"
        val service = knowledgebase_Api.retrofit.create(KnowledgebaseService::class.java)

        val requestBody = KnowledgeBaseRequest(
            title = "Preguntas",
            content = "",
            tags = listOf(4)
        )
        val call = service.getKnowledgeBaseAssigned(authHeader, requestBody)

        call.enqueue(object : Callback<List<FAQ>> {
            override fun onResponse(call: Call<List<FAQ>>, response: Response<List<FAQ>>) {
                if (response.isSuccessful) {
                    // Convertir la respuesta en un contexto de texto
                    knowledgeBaseContext = response.body()?.joinToString("\n") {
                        "Pregunta: ${it.title}\nRespuesta: ${it.content}"
                    }
                    Log.d("ChatbotFragment", "FAQs del servicio base de conocimiento: $knowledgeBaseContext")
                }
            }

            override fun onFailure(call: Call<List<FAQ>>, t: Throwable) {
                Log.e("ChatbotFragment", "Error al cargar el contexto de la base de conocimiento: ${t.message}")
            }
        })
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
            userMessageCount++
            // Agregar el mensaje del usuario a la lista
            displayedMessages.add(ChatMessage(userMessage, true))
            adapter.notifyItemInserted(displayedMessages.size - 1)
            binding.chatbotRecyclerView.scrollToPosition(displayedMessages.size - 1)

            // Vaciar la caja de texto y reiniciar el hint
            binding.chatBotEditText.text.clear()
            binding.chatBotEditText.hint = "" // Eliminar el hint

            // Verificar si el mensaje contiene palabras clave relacionadas con PQR o si es la sexta interacción
            if (shouldShowPQRSuggestion(userMessage)) {
                showPQRSuggestion()
            } else {
                // Respuesta del bot (mensaje de Cohere)
                simulateBotResponse(userMessage)
            }

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

        val apiKey = "Bearer 4FBgLHiMJTiHygmvXQ7S8Dg967i3Ludqg4xc5tFy"  //API Cohere
        //val messages = listOf(
        //    ChatGPTMessage("system", "You are a customer service chatbot specialized in complaints, claims, and petitions."),
        //    ChatGPTMessage("user", userInput)
        //)

        //val requestBody = ChatGPTRequest(
        //    model = "gpt-3.5-turbo",
        //    messages = messages,
        //    max_tokens = 50  // Limitar el número de tokens
        //)

        // Si se ha cargado el contexto de la base de conocimiento
        val prompt = if (!knowledgeBaseContext.isNullOrEmpty()) {
            "$knowledgeBaseContext\n\nUsuario: $userInput\nBot:"
        } else {
            "Usuario: $userInput\nBot:"
        }

        val requestBody = CohereRequest(prompt = prompt)
        Log.d("ChatbotFragment", "el requestbody es: $requestBody")

        //val requestBody = CohereRequest(
        //    prompt = userInput
        //)
        // Solo generar la notificación en la primera respuesta del bot
        if (isFirstBotResponse) {
            NotificationManager.addNotification(getString(R.string.bot_conversation_started))
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
                val errorMessage = getString(R.string.bot_response_error)
                if (response.isSuccessful && response.body() != null) {
                    val botReply = response.body()!!.generations.first().text
                    val truncatedReply = botReply.split(" ").take(30).joinToString(" ")
                    addMessage(truncatedReply, false)
                } else {
                    addMessage(errorMessage, false)
                }
            }

            override fun onFailure(call: Call<CohereResponse>, t: Throwable) {
                addMessage("Error: ${t.message}", false)
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



