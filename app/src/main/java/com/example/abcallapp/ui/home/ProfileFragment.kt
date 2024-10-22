package com.example.abcallapp.ui.profile

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.abcallapp.R
import com.example.abcallapp.data.model.Client
import com.example.abcallapp.data.model.ClientResponse
import com.example.abcallapp.databinding.ActivityProfileFragmentBinding
import com.example.abcallapp.network.ClientService
import com.example.abcallapp.utils.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var binding: ActivityProfileFragmentBinding
    private lateinit var userPreferences: UserPreferences
    private lateinit var clientService: ClientService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityProfileFragmentBinding.inflate(inflater, container, false)

        // Obtener la instancia de UserPreferences con requireContext()
        userPreferences = UserPreferences.getInstance(requireContext())

        // Inicializar Retrofit para ClientService
        clientService = Retrofit.Builder()
            .baseUrl("https://l36oyb6gwa.execute-api.us-east-1.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ClientService::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Realizar la consulta GET al microservicio de cliente
        fetchClientDetails()

        Log.d("ProfileFragment", "Intentando recuperar datos del usuario...")

        // Obtener los detalles del usuario desde UserPreferences
        val user = userPreferences.getUser()

        if (user != null) {
            Log.d("ProfileFragment", "Datos de usuario recuperados: ${user.name}, ${user.email}")
        } else {
            Log.e("ProfileFragment", "No se encontró usuario en SharedPreferences")
        }

        // Mostrar los datos del usuario si existen
        user?.let {
            Log.d("ProfileFragment", "Nombre: ${it.name}")
            Log.d("ProfileFragment", "Apellido: ${it.last_name}")
            Log.d("ProfileFragment", "Email: ${it.email}")
            Log.d("ProfileFragment", "Empresa (client_id): ${it.client_id}")
            Log.d("ProfileFragment", "Tipo de comunicación: ${it.communication_type}")
            val nombreCompleto = "${it.name} ${it.last_name ?: ""}"
            binding.userName.text = if (nombreCompleto.isNotBlank()) nombreCompleto else "Nombre no disponible"
            binding.userEmail.text = it.email ?: "Email no disponible"
            //binding.companyLabel.text = it.client_id.toString()
            binding.communicationLabel.text = it.communication_type ?: "Tipo de comunicación no disponible"
        }

        // Navegación al fragmento de edición al hacer clic en el botón Editar
        binding.editButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        // Iniciar la actualización de la fecha y hora
        startUpdatingTimeAndDate()
    }

    private fun startUpdatingTimeAndDate() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                updateDateTime()
                handler.postDelayed(this, 1000) // Actualiza cada 1 segundo
            }
        }
        handler.post(runnable) // Iniciar la ejecución
    }

    private fun updateDateTime() {
        val timeZone = TimeZone.getTimeZone("America/Bogota")
        val currentDate = Calendar.getInstance(timeZone).time

        // Formatos de fecha y hora
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        // Aplicar zona horaria
        dateFormat.timeZone = timeZone
        timeFormat.timeZone = timeZone

        // Actualizar UI
        binding.currentDate.text = dateFormat.format(currentDate)
        binding.currentTime.text = timeFormat.format(currentDate)
    }

    // Función para hacer la llamada GET al microservicio del cliente
    private fun fetchClientDetails() {
        val idToken = userPreferences.getIdToken()  // Recuperar el token almacenado
        if (idToken.isNullOrEmpty()) {
            Log.e("ProfileFragment", "Token de autenticación no disponible")
            return
        }

        val authHeader = "Bearer $idToken"
        val call = clientService.getClientDetails(authHeader)

        call.enqueue(object : Callback<ClientResponse> {
            override fun onResponse(call: Call<ClientResponse>, response: Response<ClientResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val client = response.body()!!.data

                    // Mostrar el nombre legal (legal_name) en la UI
                    binding.companyLabel.text = client.legal_name

                    Log.d("ClientService", "Datos del cliente obtenidos: ${client.legal_name}")
                } else {
                    Log.e("ClientService", "Error en la respuesta: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ClientResponse>, t: Throwable) {
                Log.e("ClientService", "Error en la solicitud: ${t.message}")
            }
        })
    }
}
