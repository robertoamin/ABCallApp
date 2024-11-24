package com.example.abcallapp.ui.home

import android.content.Context
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

        // Cargar la imagen del usuario desde SharedPreferences
        val sharedPrefs = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userProfileImage = sharedPrefs.getInt("user_profile_image", R.drawable.usuariochat) // Imagen por defecto

        binding.userImage.setImageResource(userProfileImage) // Configura la imagen del usuario
        binding.userImage.alpha = 0.5f // Ajuste de alpha para degradado

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
                // Mapeo de tipo de comunicación antes de guardar
                user.communication_type = mapCommunicationType(tipoComunicacion)

                saveUserChanges(user)
            } else {
                Toast.makeText(requireContext(), getString(R.string.enter_full_name), Toast.LENGTH_SHORT).show()

            }
        }

        // Cancelar y regresar a la pantalla anterior
        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp() // Regresa al fragmento anterior
        }
    }
    // Función para mapear `communication_type` de inglés a español
    private fun mapCommunicationType(type: String): String {
        return if (type == "Phone") "Telefono" else type
    }
    private fun saveUserChanges(userModificado: User) {
        val idToken = userPreferences.getIdToken()

        if (idToken.isNullOrEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.auth_token_error), Toast.LENGTH_SHORT).show()

            return
        }

        // Configurar la llamada PUT con Retrofit
        val call = userService.editUser("Bearer $idToken", userModificado)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    userPreferences.saveUser(userModificado)
                    Toast.makeText(requireContext(), getString(R.string.profile_updated), Toast.LENGTH_SHORT).show()
                    NotificationManager.addNotification(getString(R.string.profile_updated))
                    findNavController().navigateUp()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: getString(R.string.unknown_error)
                    Toast.makeText(requireContext(), getString(R.string.profile_update_error, errorMessage), Toast.LENGTH_LONG).show()
                }

            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
