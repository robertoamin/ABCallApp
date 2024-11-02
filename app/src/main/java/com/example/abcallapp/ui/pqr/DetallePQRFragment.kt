package com.example.abcallapp.ui.pqr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.abcallapp.R
import com.example.abcallapp.databinding.FragmentDetallePqrBinding
import java.text.SimpleDateFormat
import java.util.*

class DetallePQRFragment : Fragment() {

    private var _binding: FragmentDetallePqrBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetallePqrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener la fecha y hora actuales
        updateDateTime()
        // Recibir los datos del Bundle
        val subject = arguments?.getString("subject")
        val fecha = arguments?.getString("fecha")
        val estado = arguments?.getString("state")
        val estimatedCloseDate = arguments?.getString("estimated_close_date")
        val description = arguments?.getString("description")

// Limitar el texto del subject a los primeros 20 caracteres, manejando el caso en que sea null
        val truncatedSubject = if ((subject?.length ?: 0) > 20) {
            subject?.substring(0, 20) + "..."
        } else {
            subject ?: ""
        }

        // Asignar los valores a los TextView correspondientes
        binding.subjectTextView.text = truncatedSubject
        binding.AsuntoFechaTextView.text = "$fecha"
        binding.estadoTextView.text = "Estado:  $estado"
        binding.fechaCierreTextView.text = "Fecha de cierre estimada:  $estimatedCloseDate"
        binding.descripcionTextView.text = "Detalle:  $description"

        // Configurar el botón flotante para navegar al fragmento de creación de PQR
        binding.createPqrButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_create_pqr)
        }

    }
    private fun updateDateTime() {
        val timeZone = TimeZone.getTimeZone("America/Bogota")
        val currentDate = Calendar.getInstance(timeZone).time
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        // Aplicar la zona horaria correcta
        dateFormat.timeZone = timeZone

        binding.detalleFechaTextView.text = dateFormat.format(currentDate) // Actualiza la fecha
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
