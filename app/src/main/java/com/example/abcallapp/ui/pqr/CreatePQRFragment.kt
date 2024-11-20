package com.example.abcallapp.ui.pqr


import android.os.Bundle
import android.text.InputFilter
import android.util.Log
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
import com.example.abcallapp.ui.notifications.NotificationManager
import com.example.abcallapp.utils.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

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

        // Inicializar userPreferences
        val userPreferences = UserPreferences.getInstance(requireContext())
        // Obtener el idToken
        val idToken = userPreferences.getIdToken()
        // Limitar la longitud de la descripción a 500 caracteres
        binding.descripcionEditText.filters = arrayOf(InputFilter.LengthFilter(500))

        // Inicializar el servicio de Retrofit
        pqrService = ApiClient.retrofit.create(PQRService::class.java)

        // Configurar adaptador para el AutoCompleteTextView (dropdown de tipos de PQR)
        val tipoPqrOptions = resources.getStringArray(R.array.tipo_pqr_opciones)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, tipoPqrOptions)
        binding.tipoPqrDropdown.setAdapter(adapter)

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        binding.fechaTextView.setText(currentDate)


        // Acción del botón "Crear"
        binding.createButton.setOnClickListener {
            // Obtener los datos del formulario
            val title = binding.tituloEditText.text.toString()
            val description = binding.descripcionEditText.text.toString()
            val type = binding.tipoPqrDropdown.text.toString()
            // Mapea el valor de `type` a español antes de enviarlo
            val typeInSpanish = mapTypeToSpanish(type)
            // Verificar que los campos y el idToken no sean nulos o estén vacíos
            if (title.isNotEmpty() && description.isNotEmpty() && typeInSpanish.isNotEmpty()) {
                if (!idToken.isNullOrEmpty()) {
                    val nuevoPQR = PQR(title, description, typeInSpanish)
                    enviarPQR(nuevoPQR, idToken)
                } else {
                    Toast.makeText(requireContext(), "No se encontró un idToken válido.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), getString(R.string.incomplete_data), Toast.LENGTH_SHORT).show()
            }

        }

        // Acción del botón "Cancelar"
        binding.cancelButton.setOnClickListener {
            requireActivity().onBackPressed() // Regresa a la pantalla anterior
        }
    }

    // Método para enviar el PQR al microservicio
    private fun enviarPQR(pqr: PQR, idToken: String) {
        if (idToken.isNotEmpty()) {
            val authHeader = "Bearer $idToken"
            Log.d("CreatePQR", "Enviando PQR al microservicio: Title=${pqr.title}, Description=${pqr.description}, Type=${pqr.type}")

            val call = pqrService.createPQR(authHeader, pqr)
            call.enqueue(object : Callback<PQRResponse> {
                override fun onResponse(call: Call<PQRResponse>, response: Response<PQRResponse>) {
                    if (response.isSuccessful) {
                        val pqrResponse = response.body()
                        if (pqrResponse != null && pqrResponse.status == "ok") {
                            Log.d("CreatePQR", "PQR creado exitosamente en el microservicio.")
                            val notificationMessage = getString(R.string.new_pqr_created, pqrResponse.ticket_number)
                            NotificationManager.addNotification(notificationMessage)


                            showSuccessDialog(pqrResponse.ticket_number)
                        } else {
                            Log.e("CreatePQR", "Error en la respuesta del servidor: ${pqrResponse?.status}")
                            Toast.makeText(requireContext(), getString(R.string.pqr_creation_error), Toast.LENGTH_SHORT).show()

                        }
                    } else {
                        Log.e("CreatePQR", "Error al crear el PQR. Código de respuesta: ${response.code()}, Error: ${response.errorBody()?.string()}")
                        Toast.makeText(requireContext(), getString(R.string.PQR_error), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<PQRResponse>, t: Throwable) {
                    Log.e("CreatePQR", "Error de red al intentar crear el PQR: ${t.message}")
                    val networkErrorMessage = getString(R.string.network_error, t.message)
                    Toast.makeText(requireContext(), networkErrorMessage, Toast.LENGTH_SHORT).show()

                }
            })
        } else {
            Log.e("CreatePQR", "No se encontró un idToken válido.")
            Toast.makeText(requireContext(), getString(R.string.auth_request_error), Toast.LENGTH_SHORT).show()
        }
    }
    // Función para mapear `type` de inglés a español
    private fun mapTypeToSpanish(type: String): String {
        return when (type) {
            "Request" -> "Peticion"
            "Complaint" -> "Queja"
            "Claim" -> "Reclamo"
            else -> type // Si no coincide, devuelve el valor tal como está
        }
    }
    // Método para mostrar el diálogo de éxito
    private fun showSuccessDialog(ticketNumber: String) {
        AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(getString(R.string.success))
            .setMessage(getString(R.string.PQR_success) + "\nTicket #: $ticketNumber")
            .setPositiveButton(getString(R.string.Accept)) { dialog, _ ->
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