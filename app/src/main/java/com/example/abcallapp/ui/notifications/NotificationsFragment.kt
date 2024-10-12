package com.example.abcallapp.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abcallapp.R
import com.example.abcallapp.data.model.NotificationItem
import com.example.abcallapp.databinding.FragmentNotificationsBinding
import com.example.abcallapp.ui.adapters.NotificationsAdapter

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Simulando una lista de notificaciones
        val mockNotifications = listOf(
            NotificationItem(R.drawable.notifications_active_24px, "Nuevo PQR creado"),
            NotificationItem(R.drawable.notifications_active_24px, "PQR #3 solucionado"),
            NotificationItem(R.drawable.notifications_active_24px, "Perfil modificado"),
            NotificationItem(R.drawable.notifications_active_24px, "Conversación registrada"),
            NotificationItem(R.drawable.notifications_active_24px, "Nuevo PQR creado"),
            NotificationItem(R.drawable.notifications_active_24px, "PQR #4 solucionado")
        )

        val adapter = NotificationsAdapter(mockNotifications)
        binding.notificationsRecyclerView.adapter = adapter
        binding.notificationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
