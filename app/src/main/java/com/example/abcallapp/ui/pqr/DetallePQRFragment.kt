package com.example.abcallapp.ui.pqr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.abcallapp.R
import com.example.abcallapp.databinding.FragmentDetallePqrBinding

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

        // Recibir los datos del Bundle
        val subject = arguments?.getString("subject")
        val fecha = arguments?.getString("fecha")
        val estado = arguments?.getString("state")
        val estimatedCloseDate = arguments?.getString("estimated_close_date")
        val description = arguments?.getString("description")

        // Asignar los valores a los TextView correspondientes
        binding.subjectTextView.text = "Asunto: $subject"
        binding.detalleFechaTextView.text = "Fecha: $fecha"
        binding.estadoTextView.text = "Estado: $estado"
        binding.fechaCierreTextView.text = "Fecha de cierre estimada: $estimatedCloseDate"
        binding.descripcionTextView.text = description

        // Configurar el botón flotante para navegar al fragmento de creación de PQR
        binding.createPqrButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_create_pqr)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
