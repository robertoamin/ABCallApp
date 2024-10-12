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

        // Recibir datos del PQR seleccionado desde HomeFragment (Mock data por ahora)
        val fecha = arguments?.getString("fecha") ?: "Sept 17, 2024"
        val empresa = arguments?.getString("empresa") ?: "Claro"
        val detalle = arguments?.getString("detalle") ?: "Facturación equivocada"
        val estado = arguments?.getString("estado") ?: "Abierto"
        val fechaCierre = arguments?.getString("fechaCierre") ?: "20/11/2024"

        // Configurar la vista con los datos
        binding.detalleFechaTextView.text = fecha
        binding.empresaTextView.text = "Empresa: $empresa"
        binding.detallePqrTextView.text = detalle
        binding.estadoTextView.text = "Estado: $estado"
        binding.fechaCierreTextView.text = "Fecha estimada de cierre: $fechaCierre"

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
