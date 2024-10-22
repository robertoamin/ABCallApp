package com.example.abcallapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.abcallapp.R
import com.example.abcallapp.adapters.PQRAdapter
import com.example.abcallapp.data.model.PQRItem
import com.example.abcallapp.databinding.FragmentHomeBinding
import com.example.abcallapp.network.PQRClient
import com.example.abcallapp.network.PQRService
import com.example.abcallapp.utils.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

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

        // Inicializar Retrofit para el servicio de PQRs
        val pqrService = PQRClient.retrofit.create(PQRService::class.java)

        // Llamar a la función que obtiene los PQRs del microservicio
        getPQRs(pqrService)

        // Navegación al fragmento de ChatBot
        binding.chatBotEditText.setOnClickListener {
            findNavController().navigate(R.id.navigation_chatbot)
        }

        // Manejo del botón flotante para creación de PQRs
        binding.createPqrButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_create_pqr)
        }
    }

    // Esta función realiza la petición de los PQRs al microservicio y configura el RecyclerView
    private fun getPQRs(pqrService: PQRService) {
        // Obtener el idToken de SharedPreferences
        val userPrefs = UserPreferences.getInstance(requireContext())
        val idToken = userPrefs.getIdToken()

        // Verificar si el idToken no es nulo
        if (idToken.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "No se encontró el token de autenticación", Toast.LENGTH_SHORT).show()
            return
        }

        // Llamar al servicio PQR con el token de autorización
        val authHeader = "Bearer $idToken"
        val call = pqrService.getPQRs(authHeader)
        call.enqueue(object : Callback<List<PQRItem>> {
            override fun onResponse(call: Call<List<PQRItem>>, response: Response<List<PQRItem>>) {
                if (response.isSuccessful && response.body() != null) {
                    val pqrList = response.body()!!

                    // Configurar RecyclerView con los datos obtenidos del microservicio
                    setupRecyclerView(pqrList)
                } else {
                    // En caso de error, mostrar un mensaje de error al usuario
                    Toast.makeText(requireContext(), getString(R.string.PQR_get_error), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<PQRItem>>, t: Throwable) {
                // En caso de fallo en la red, mostrar un mensaje de error
                Toast.makeText(requireContext(), "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    // Esta función configura el RecyclerView con la lista de PQR obtenida
    private fun setupRecyclerView(pqrList: List<PQRItem>) {
        val pqrAdapter = PQRAdapter(pqrList) { pqrItem ->
            // Pasar los datos de PQR al fragmento de detalle
            val bundle = Bundle().apply {
                putString("fecha", pqrItem.date)
                putString("subject", pqrItem.subject)
                putString("state", pqrItem.status)
                putString("estimated_close_date", pqrItem.estimated_close_date)
                putString("description", pqrItem.description)
            }
            findNavController().navigate(R.id.action_home_to_detallePQRFragment, bundle)
        }

        // Configuración del RecyclerView
        binding.pqrRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.pqrRecyclerView.adapter = pqrAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
