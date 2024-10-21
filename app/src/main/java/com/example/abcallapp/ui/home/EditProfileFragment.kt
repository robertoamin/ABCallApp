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
import com.example.abcallapp.databinding.ActivityEditProfileFragmentBinding
import com.example.abcallapp.network.ApiClient
import com.example.abcallapp.network.ProfileService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditProfileFragment : Fragment() {

    private lateinit var binding: ActivityEditProfileFragmentBinding
    private lateinit var tipoComunicacion: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityEditProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuración del AutoCompleteTextView con opciones
        val opcionesComunicacion = resources.getStringArray(R.array.tipo_comunicacion_opciones)
        val adapter = ArrayAdapter(requireContext(), R.layout.communication_dropdown_item, opcionesComunicacion)
        binding.tipoComunicacionEditText.setAdapter(adapter)

        // Guardar cambios cuando el usuario presione "Guardar"
        binding.savePerfilButton.setOnClickListener {
            tipoComunicacion = binding.tipoComunicacionEditText.text.toString()
            if (tipoComunicacion.isNotEmpty()) {
                guardarCambiosEnMicroservicio(tipoComunicacion)
            }
        }

        // Cancelar y regresar a la pantalla anterior
        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp() // Regresa al fragmento anterior
        }
    }

    private fun guardarCambiosEnMicroservicio(tipoComunicacion: String) {
        // Crear el objeto de datos para enviar
        val perfilModificado = hashMapOf("tipoComunicacion" to tipoComunicacion)

        // Configurar la llamada PUT con Retrofit (ejemplo)
        val apiService = ApiClient.retrofit.create(ProfileService::class.java)
        val call = apiService.actualizarPerfil(perfilModificado)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp() // Regresa al fragmento anterior
                } else {
                    Toast.makeText(requireContext(), "Error al actualizar el perfil", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
