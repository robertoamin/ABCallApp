package com.example.abcallapp.ui.notifications

import com.example.abcallapp.R
import com.example.abcallapp.data.model.NotificationItem

object NotificationManager {
    private val notifications = mutableListOf<NotificationItem>()

    fun addNotification(message: String) {
        notifications.add(NotificationItem(R.drawable.notifications_active_24px, message))
    }

    fun getNotifications(): List<NotificationItem> {
        return notifications
    }
}
