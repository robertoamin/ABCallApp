package com.example.abcallapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abcallapp.R
import com.example.abcallapp.adapters.PQRAdapter
import com.example.abcallapp.data.model.PQRItem
import com.example.abcallapp.databinding.FragmentHomeBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Datos Mock
        val mockPQRList = listOf(
            PQRItem("13/10/2024", "Solicitud duplicado factura", "Abierto"),
            PQRItem("12/10/2024", "Queja por servicio", "Cerrado"),
            PQRItem("11/10/2024", "Reclamo de devolución", "Abierto")
        )

        // Configurar RecyclerView con el listener
        val pqrAdapter = PQRAdapter(mockPQRList) { pqrItem ->
            val bundle = Bundle().apply {
                putString("fecha", pqrItem.date)
                putString("empresa", "Claro")  // Dato temporal
                putString("detalle", pqrItem.title)
                putString("estado", pqrItem.status)
                putString("fechaCierre", "20/11/2024")  // Dato temporal
            }
            findNavController().navigate(R.id.action_home_to_detallePQRFragment, bundle)
        }

        // Configurar RecyclerView
        binding.pqrRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.pqrRecyclerView.adapter = pqrAdapter

        // Navegación al fragmento de ChatBot al hacer clic en el EditText
        binding.chatBotEditText.setOnClickListener {
            findNavController().navigate(R.id.navigation_chatbot)
        }

        // Manejo del clic del botón flotante
        val createPqrButton: FloatingActionButton = binding.createPqrButton
        createPqrButton.setOnClickListener {
            // Navegar al fragmento de creación de PQR
            findNavController().navigate(R.id.navigation_create_pqr)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
