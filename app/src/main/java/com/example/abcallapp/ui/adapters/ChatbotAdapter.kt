package com.example.abcallapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.abcallapp.R
import com.example.abcallapp.data.model.ChatMessage

class ChatbotAdapter(private val messageList: List<ChatMessage>) :
    RecyclerView.Adapter<ChatbotAdapter.ChatMessageViewHolder>() {

    class ChatMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        val messageIcon: ImageView = itemView.findViewById(R.id.messageIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatMessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val chatMessage = messageList[position]

        if (chatMessage.isUser) {
            // Configura el texto del usuario: blanco, alineado a la izquierda
            holder.messageTextView.apply {
                text = chatMessage.text
                textAlignment = View.TEXT_ALIGNMENT_VIEW_START // Justificado a la izquierda
                setTextColor(ContextCompat.getColor(context, android.R.color.white)) // Texto blanco
                holder.messageIcon.visibility = View.VISIBLE // Mostrar icono de chat para el usuario
            }
        } else {
            // Configura el texto del bot: teal, alineado a la derecha
            holder.messageTextView.apply {
                text = chatMessage.text
                textAlignment = View.TEXT_ALIGNMENT_VIEW_END // Justificado a la derecha
                setTextColor(ContextCompat.getColor(context, R.color.teal_200)) // Texto color teal
                holder.messageIcon.visibility = View.GONE // No mostrar icono para el bot
            }
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}
