package com.example.abcallapp.ui.pqr

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AlertDialog
import com.example.abcallapp.R
import com.example.abcallapp.databinding.FragmentCreatePqrBinding
import com.example.abcallapp.network.ApiClient
import com.example.abcallapp.network.PQRService
import com.example.abcallapp.data.model.PQR
import com.example.abcallapp.data.model.PQRResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreatePQRFragment : Fragment() {

    private var _binding: FragmentCreatePqrBinding? = null
    private val binding get() = _binding!!
    private lateinit var pqrService: PQRService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePqrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar el servicio de Retrofit
        pqrService = ApiClient.retrofit.create(PQRService::class.java)

        // Configurar adaptador para el AutoCompleteTextView (dropdown de tipos de PQR)
        val tipoPqrOptions = resources.getStringArray(R.array.tipo_pqr_opciones)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, tipoPqrOptions)
        binding.tipoPqrDropdown.setAdapter(adapter)


        // Acción del botón "Crear"
        binding.createButton.setOnClickListener {
            // Obtener los datos del formulario
            val subject = binding.tituloEditText.text.toString()
            val description = binding.descripcionEditText.text.toString()
            val type = binding.tipoPqrDropdown.text.toString()


            // Validar campos antes de enviar
            if (subject.isNotEmpty() && description.isNotEmpty() && type.isNotEmpty()) {
                // Crear el objeto PQR y llamar al servicio
                val nuevoPQR = PQR(subject, description, type)
                enviarPQR(nuevoPQR)
            } else {
                Toast.makeText(requireContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Acción del botón "Cancelar"
        binding.cancelButton.setOnClickListener {
            requireActivity().onBackPressed() // Regresa a la pantalla anterior
        }
    }

    // Método para enviar el PQR al microservicio
    private fun enviarPQR(pqr: PQR) {
        val call = pqrService.createPQR(pqr)
        call.enqueue(object : Callback<PQRResponse> {
            override fun onResponse(call: Call<PQRResponse>, response: Response<PQRResponse>) {
                if (response.isSuccessful && response.body()?.statusCode == 200) {
                    showSuccessDialog()
                } else {
                    Toast.makeText(requireContext(), "Error al crear el PQR", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<PQRResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Método para mostrar el diálogo de éxito
    private fun showSuccessDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Éxito")
            .setMessage("Su PQR ha sido creado con éxito")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                requireActivity().onBackPressed() // Regresa a la pantalla anterior después de confirmar
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}