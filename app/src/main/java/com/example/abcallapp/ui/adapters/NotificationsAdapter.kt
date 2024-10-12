package com.example.abcallapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.abcallapp.R
import com.example.abcallapp.data.model.NotificationItem

class NotificationsAdapter(private val notifications: List<NotificationItem>) :
    RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImageView: ImageView = itemView.findViewById(R.id.iconImageView)
        val notificationTextView: TextView = itemView.findViewById(R.id.notificationTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.iconImageView.setImageResource(notification.iconRes)
        holder.notificationTextView.text = notification.message
    }

    override fun getItemCount(): Int {
        return notifications.size
    }
}
