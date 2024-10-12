package com.example.abcallapp.data.model

import com.example.abcallapp.R

data class NotificationItem(val iconRes: Int, val message: String)

val mockNotifications = listOf(
    NotificationItem(R.drawable.notifications_active_24px, "Nuevo PQR creado"),
    NotificationItem(R.drawable.notifications_active_24px, "PQR #3 solucionado"),
    NotificationItem(R.drawable.notifications_active_24px, "Perfil modificado"),
    NotificationItem(R.drawable.notifications_active_24px, "Conversación registrada"),
    NotificationItem(R.drawable.notifications_active_24px, "Nuevo PQR creado"),
    NotificationItem(R.drawable.notifications_active_24px, "PQR #4 solucionado")
)

