package com.example.abcallapp.adapters

import android.content.Context
import android.text.SpannableString
import android.text.method.LinkMovementMethod
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

class ChatbotAdapter(private val messageList: List<ChatMessage>, private val context: Context) :
    RecyclerView.Adapter<ChatbotAdapter.ChatMessageViewHolder>() {

    private val sharedPrefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val userProfileImage = sharedPrefs.getInt("user_profile_image", R.drawable.usuariochat) // Imagen por defecto

    class ChatMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        val botIcon: ImageView = itemView.findViewById(R.id.botIcon)
        val userIcon: ImageView = itemView.findViewById(R.id.userIcon)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatMessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val chatMessage = messageList[position]

        if (chatMessage.isUser) {
            holder.messageTextView.apply {
                text = chatMessage.text
                setTextColor(ContextCompat.getColor(context, R.color.white))
                setBackgroundResource(R.drawable.message_background_user)
            }
            holder.botIcon.visibility = View.GONE
            holder.userIcon.visibility = View.VISIBLE
            holder.userIcon.setImageResource(userProfileImage) // Configura la imagen del usuario
            (holder.itemView as LinearLayout).gravity = Gravity.END
        } else {
            holder.messageTextView.apply {
                text = chatMessage.text
                setTextColor(ContextCompat.getColor(context, R.color.teal_200))
                setBackgroundResource(R.drawable.message_background_bot)
                movementMethod = LinkMovementMethod.getInstance()
            }
            holder.botIcon.visibility = View.VISIBLE
            holder.userIcon.visibility = View.GONE
            (holder.itemView as LinearLayout).gravity = Gravity.START
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }
}
