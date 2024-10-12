package com.example.abcallapp.ui.login

import android.content.Intent
import android.os.Bundle
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
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation
import com.amazonaws.regions.Regions
import com.example.abcallapp.R
import com.example.abcallapp.ui.home.HomeActivity
import com.google.android.material.textfield.TextInputEditText

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
            authenticateUser(username, password)
        } else {
            Toast.makeText(this, "Por favor, ingresa tu usuario y contraseña", Toast.LENGTH_SHORT).show()
        }
    }

    private fun authenticateUser(username: String, password: String) {
        val authenticationHandler = object : AuthenticationHandler {
            override fun onSuccess(userSession: CognitoUserSession, newDevice: CognitoDevice?) {
                val idToken = userSession.idToken
                val accessToken = userSession.accessToken

                if (idToken != null && idToken.jwtToken.isNotEmpty()) {
                    Log.d("Cognito", "Login success: ${idToken.jwtToken}")
                    Toast.makeText(this@LoginActivity, "Login exitoso", Toast.LENGTH_SHORT).show()

                    // Redirigir a la pantalla principal después de un login exitoso
                    val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.e("Cognito", "Login failed: idToken is null")
                    Toast.makeText(this@LoginActivity, "Error: idToken no válido", Toast.LENGTH_LONG).show()
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
                //continuation?.setMfaCode("el código de MFA")
                //continuation?.continueTask()
            }

            override fun onFailure(exception: Exception) {
                Log.e("Cognito", "Login failed: ${exception.localizedMessage}")
                exception.printStackTrace()  // Esto te dará más detalles en el log
                Toast.makeText(this@LoginActivity, "Error en la autenticación: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
            }


            override fun authenticationChallenge(continuation: com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation?) {
                // Manejar desafíos adicionales (MFA, etc.)
            }
        }

        // Iniciar el proceso de autenticación
        val user = userPool.getUser(username)
        user.getSessionInBackground(authenticationHandler)
    }
}
