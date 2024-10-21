package com.example.abcallapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.regions.Regions
import com.example.abcallapp.R
import com.example.abcallapp.data.model.User
import com.example.abcallapp.network.UserService
import com.example.abcallapp.ui.home.HomeActivity
import com.example.abcallapp.utils.UserPreferences
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var userPool: CognitoUserPool
    private lateinit var usernameInput: TextInputEditText
    private lateinit var passwordInput: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Vinculando los EditText desde el XML
        usernameInput = findViewById(R.id.usernameEditText)
        passwordInput = findViewById(R.id.passwordEditText)

        // Inicializa el CognitoUserPool
        userPool = CognitoUserPool(
            this,
            "us-east-1_YDIpg1HiU",
            "65sbvtotc1hssqecgusj1p3f9g",
            null,
            Regions.US_EAST_1
        )
    }

    fun onLoginButtonClick(view: View) {
        val username = usernameInput.text.toString()
        val password = passwordInput.text.toString()

        if (username.isNotEmpty() && password.isNotEmpty()) {
            Log.d("username", "Username: ${username}")
            Log.d("password", "Password: ${password}")
            authenticateUser(username, password)
        } else {
            Toast.makeText(this, getString(R.string.login_empty_fields), Toast.LENGTH_SHORT).show()
        }
    }

    private fun authenticateUser(username: String, password: String) {
        val authenticationHandler = object : AuthenticationHandler {
            override fun onSuccess(userSession: CognitoUserSession, newDevice: CognitoDevice?) {
                val idToken = userSession.idToken
                val accessToken = userSession.accessToken  // lo necesito para el Auth de otras API

                if (idToken != null && idToken.jwtToken.isNotEmpty()) {
                    Log.d("Cognito", "Login success: ${idToken.jwtToken}")
                    Toast.makeText(this@LoginActivity, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    Log.d("Cognito", "Access Token: ${accessToken.jwtToken}") // obtengo el auth

                    // Guardar el idToken en SharedPreferences
                    val userPrefs = UserPreferences.getInstance(this@LoginActivity)
                    userPrefs.saveIdToken(idToken.jwtToken)

                    // Llamar al microservicio para obtener los detalles del usuario usando el access token
                    fetchUserDetails(idToken.jwtToken)

                    // Redirigir a la pantalla principal después de un login exitoso
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("Cognito", "Login failed: idToken is null")
                    Toast.makeText(this@LoginActivity, getString(R.string.login_error), Toast.LENGTH_LONG).show()
                }
            }

            override fun getAuthenticationDetails(authenticationContinuation: AuthenticationContinuation, userId: String) {
                // Proporciona los detalles de autenticación
                val authDetails = AuthenticationDetails(userId, password, null)
                authenticationContinuation.setAuthenticationDetails(authDetails)
                authenticationContinuation.continueTask()
            }

            override fun getMFACode(continuation: MultiFactorAuthenticationContinuation?) {
                // Si se requiere MFA (autenticación multifactor), puedes obtener el código MFA aquí
            }

            override fun onFailure(exception: Exception) {
                Log.e("Cognito", "Login failed: ${exception.localizedMessage}")
                exception.printStackTrace()
                Toast.makeText(this@LoginActivity, "Error en la autenticación: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
            }

            override fun authenticationChallenge(continuation: ChallengeContinuation?) {
                // Manejar desafíos adicionales (MFA, etc.)
            }
        }

        // Iniciar el proceso de autenticación
        val user = userPool.getUser(username)
        user.getSessionInBackground(authenticationHandler)
    }

    // Función para hacer la llamada GET al microservicio
    private fun fetchUserDetails(idToken: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://1acgpw2vfg.execute-api.us-east-1.amazonaws.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(UserService::class.java)
        val authHeader = "Bearer $idToken"  // Usar idToken en lugar de accessToken

        val call = service.getUserDetails(authHeader)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        // Registrar todos los detalles del usuario en Logcat
                        Log.d("UserService", "Nombre: ${it.name}")
                        Log.d("UserService", "Apellido: ${it.last_name}")
                        Log.d("UserService", "Email: ${it.email}")
                        Log.d("UserService", "Client ID: ${it.client_id}")
                        Log.d("UserService", "Documento: ${it.document_type}")
                        Log.d("UserService", "Rol: ${it.user_role}")
                        Log.d("UserService", "Número de ID: ${it.id_number}")
                        Log.d("UserService", "Teléfono: ${it.cellphone}")
                        Log.d("UserService", "Tipo de Comunicación: ${it.communication_type}")

                        // Guardar la información del usuario en SharedPreferences
                        val userPrefs = UserPreferences.getInstance(this@LoginActivity)
                        userPrefs.saveUser(it)
                        Log.d("LoginActivity", "Usuario guardado en SharedPreferences: ${it.name} ${it.last_name}")
                    }
                } else {
                    Log.e("UserService", "Error: ${response.code()}")
                    Log.e("UserService", "Error body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("UserService", "Error en la solicitud: ${t.message}")
            }
        })
    }

    // Función para decodificar el JWT y obtener los claims
    private fun getClaimsFromJWT(jwt: String): Map<String, Any> {
        try {
            // El JWT tiene tres partes separadas por puntos (header, payload, signature)
            val parts = jwt.split(".")
            if (parts.size == 3) {
                val payload = parts[1]
                // Decodificar la parte de los claims (payload)
                val decodedPayload = String(Base64.decode(payload, Base64.URL_SAFE))
                // Convertir el payload en un Map
                val gson = Gson()
                val mapType = object : TypeToken<Map<String, Any>>() {}.type
                return gson.fromJson(decodedPayload, mapType)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyMap()
    }
}

