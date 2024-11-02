package com.example.abcallapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.abcallapp.R
import com.example.abcallapp.data.model.User
import com.example.abcallapp.databinding.ActivityEditProfileFragmentBinding
import com.example.abcallapp.network.UserClient
import com.example.abcallapp.network.UserService
import com.example.abcallapp.ui.notifications.NotificationManager
import com.example.abcallapp.utils.UserPreferences
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileFragment : Fragment() {

    private lateinit var binding: ActivityEditProfileFragmentBinding
    private lateinit var userPreferences: UserPreferences
    private lateinit var userService: UserService
    private lateinit var user: User
    private lateinit var tipoComunicacion: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityEditProfileFragmentBinding.inflate(inflater, container, false)

        // Inicializa UserPreferences para obtener los datos del usuario
        userPreferences = UserPreferences.getInstance(requireContext())

        // Inicializa el servicio de Retrofit
        userService = UserClient.retrofit.create(UserService::class.java)

        // Cargar el usuario desde las preferencias
        user = userPreferences.getUser() ?: return null

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Establecer el nombre y el email en la UI
        val nombreCompleto = "${user.name} ${user.last_name ?: ""}"
        binding.userName.text = if (nombreCompleto.isNotBlank()) nombreCompleto else "Nombre no disponible"
        binding.userEmail.text = user.email ?: "Email no disponible"

        binding.tipoComunicacionEditText.setText(user.communication_type)

        // Configuración del AutoCompleteTextView con opciones
        val opcionesComunicacion = resources.getStringArray(R.array.tipo_comunicacion_opciones)
        val adapter = ArrayAdapter(requireContext(), R.layout.communication_dropdown_item, opcionesComunicacion)
        binding.tipoComunicacionEditText.setAdapter(adapter)

        // Guardar cambios cuando el usuario presione "Guardar"
        binding.savePerfilButton.setOnClickListener {
            tipoComunicacion = binding.tipoComunicacionEditText.text.toString()
            val fullNameInput = binding.userNameEditText.text.toString().trim()

            if (fullNameInput.isNotEmpty()) {
                val nameParts = fullNameInput.split(" ")
                val firstName = nameParts.firstOrNull() ?: ""
                val lastName = nameParts.drop(1).joinToString(" ")

                user.name = firstName
                user.last_name = lastName
                user.communication_type = tipoComunicacion

                saveUserChanges(user)
            } else {
                Toast.makeText(requireContext(), "Por favor ingrese el nombre completo", Toast.LENGTH_SHORT).show()
            }
        }

        // Cancelar y regresar a la pantalla anterior
        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp() // Regresa al fragmento anterior
        }
    }

    private fun saveUserChanges(userModificado: User) {
        val idToken = userPreferences.getIdToken()

        if (idToken.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "No se pudo obtener el token de autenticación", Toast.LENGTH_SHORT).show()
            return
        }

        // Configurar la llamada PUT con Retrofit
        val call = userService.editUser("Bearer $idToken", userModificado)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    // Guardar el usuario actualizado en las preferencias
                    userPreferences.saveUser(userModificado)

                    Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    NotificationManager.addNotification("Perfil actualizado")

                    findNavController().navigateUp() // Regresa al fragmento anterior
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    Toast.makeText(requireContext(), "Error al actualizar el perfil: $errorMessage", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
