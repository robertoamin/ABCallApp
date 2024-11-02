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
import java.text.SimpleDateFormat
import java.util.*

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
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

        // Obtener el mes y el año actual en formato deseado
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        var currentMonthYear = dateFormat.format(currentDate)

        // Asegurarse que el mes comienza con mayúscula y el resto está en minúscula
        currentMonthYear = currentMonthYear.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        // Establecer el mes y año en el TextView
        binding.dateTextView.text = currentMonthYear

        // Obtener las notificaciones del NotificationManager
        val notifications = NotificationManager.getNotifications()

        // Simulando una lista de notificaciones
    //    val mockNotifications = listOf(
    //        NotificationItem(R.drawable.notifications_active_24px, "Nuevo PQR creado"),
    //        NotificationItem(R.drawable.notifications_active_24px, "PQR #3 solucionado"),
    //        NotificationItem(R.drawable.notifications_active_24px, "Perfil modificado"),
    //        NotificationItem(R.drawable.notifications_active_24px, "Conversación registrada"),
    //        NotificationItem(R.drawable.notifications_active_24px, "Nuevo PQR creado"),
    //        NotificationItem(R.drawable.notifications_active_24px, "PQR #4 solucionado")
    //    )
        val adapter = NotificationsAdapter(notifications)
        //    val adapter = NotificationsAdapter(mockNotifications)
        binding.notificationsRecyclerView.adapter = adapter
        binding.notificationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())


    }

    override fun onResume() {
        super.onResume()
        // Recargar notificaciones cuando el fragmento se vuelve visible
        val notifications = NotificationManager.getNotifications()
        val adapter = NotificationsAdapter(notifications)
        binding.notificationsRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
