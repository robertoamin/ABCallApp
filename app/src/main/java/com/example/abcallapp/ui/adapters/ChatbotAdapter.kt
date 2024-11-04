package com.example.abcallapp.adapters

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
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
            // Configura el texto del usuario
            holder.messageTextView.apply {
                text = chatMessage.text
                setTextColor(ContextCompat.getColor(context, R.color.white))
                setBackgroundResource(R.drawable.message_background_user)
            }
            holder.messageIcon.visibility = View.GONE // Ocultar ícono del usuario
            (holder.itemView as LinearLayout).gravity = Gravity.END // Alinea a la derecha

        } else {
            // Configura el texto del bot
            holder.messageTextView.apply {
                text = chatMessage.text
                setTextColor(ContextCompat.getColor(context, R.color.teal_200))
                setBackgroundResource(R.drawable.message_background_bot)
            }
            holder.messageIcon.visibility = View.VISIBLE // Mostrar ícono para el bot
            (holder.itemView as LinearLayout).gravity = Gravity.START // Alinea a la izquierda
        }
    }



    override fun getItemCount(): Int {
        return messageList.size
    }
}
