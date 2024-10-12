package com.example.abcallapp.ui.chatbot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abcallapp.R
import com.example.abcallapp.adapters.ChatbotAdapter
import com.example.abcallapp.databinding.FragmentChatbotBinding
import com.example.abcallapp.data.model.ChatMessage

class ChatbotFragment : Fragment() {

    private var _binding: FragmentChatbotBinding? = null
    private val binding get() = _binding!!

    // Lista de mensajes de la conversación
    private val conversation = listOf(
        ChatMessage("Hola, ¿cómo te podemos ayudar?", false),   // Mensaje del bot
        ChatMessage("Necesito una copia de mi factura.", true), // Mensaje del usuario
        ChatMessage("Enseguida le enviamos la copia.", false),  // Mensaje del bot
        ChatMessage("Muchas Gracias!.", true)                  // Mensaje del usuario
    )

    private val displayedMessages = mutableListOf<ChatMessage>() // Mensajes mostrados
    private lateinit var adapter: ChatbotAdapter
    private var currentIndex = 0 // Para seguir el orden de los mensajes

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatbotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar el RecyclerView con el adaptador
        adapter = ChatbotAdapter(displayedMessages)
        binding.chatbotRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatbotRecyclerView.adapter = adapter

        // Mostrar el primer mensaje (del bot)
        addNextMessage()

        // Manejar el evento de clic del botón "Enviar"
        binding.sendButton.setOnClickListener {
            addNextMessage() // Agregar el siguiente mensaje a la conversación
        }
        // Acción del botón "Cancelar"
        binding.cancelButton.setOnClickListener {
            // Regresa al HomeFragment
            findNavController().navigate(R.id.navigation_home)
        }
    }

    // Función para agregar el siguiente mensaje a la lista
    private fun addNextMessage() {
        if (currentIndex < conversation.size) {
            displayedMessages.add(conversation[currentIndex]) // Agregar el siguiente mensaje
            adapter.notifyItemInserted(displayedMessages.size - 1) // Notificar al adaptador
            binding.chatbotRecyclerView.scrollToPosition(displayedMessages.size - 1) // Desplazar hacia abajo
            currentIndex++ // Avanzar al siguiente mensaje
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


