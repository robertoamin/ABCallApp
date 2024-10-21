package com.example.abcallapp.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.abcallapp.data.model.User


class UserPreferences private constructor(context: Context) {

    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "name"
        private const val KEY_USER_LAST_NAME = "last_name"
        private const val KEY_USER_EMAIL = "email"
        private const val KEY_USER_ROLE = "role"
        private const val KEY_DOCUMENT_TYPE = "document_type"
        private const val KEY_COMMUNICATION_TYPE = "communication_type"
        private const val KEY_CLIENT_ID = "client_id"
        private const val KEY_USERNAME = "username"

        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferences(context).also { INSTANCE = it }
            }
        }
    }

    // Guardar información del usuario en SharedPreferences
    fun saveUser(user: User) {
        val editor = preferences.edit()
        editor.putString(KEY_USER_ID, user.id_number)
        editor.putString(KEY_USER_NAME, user.name)
        editor.putString(KEY_USER_LAST_NAME, user.last_name)
        editor.putString(KEY_USER_EMAIL, user.email)
        editor.putString(KEY_USER_ROLE, user.user_role)
        editor.putString(KEY_DOCUMENT_TYPE, user.document_type)
        editor.putString(KEY_COMMUNICATION_TYPE, user.communication_type)
        editor.putInt(KEY_CLIENT_ID, user.client_id)
        editor.putString(KEY_USERNAME, user.username) // Guardar username
        editor.apply()

        // Verificar si los datos se están guardando
        Log.d("UserPreferences", "Usuario guardado: ${user.name} ${user.last_name}, Email: ${user.email}")
    }

    // Leer información del usuario
    fun getUser(): User? {
        val userId = preferences.getString(KEY_USER_ID, null)
        val userName = preferences.getString(KEY_USER_NAME, null)
        val userLastName = preferences.getString(KEY_USER_LAST_NAME, null)
        val userEmail = preferences.getString(KEY_USER_EMAIL, null)
        val userRole = preferences.getString(KEY_USER_ROLE, null)
        val documentType = preferences.getString(KEY_DOCUMENT_TYPE, null)
        val communicationType = preferences.getString(KEY_COMMUNICATION_TYPE, null)
        val clientId = preferences.getInt(KEY_CLIENT_ID, -1)
        val username = preferences.getString(KEY_USERNAME, null) // Recuperar username

        Log.d("UserPreferences", "Intentando recuperar datos del usuario: $userName $userLastName $userEmail")

        return if (userId != null && userName != null && userEmail != null && userRole != null && documentType != null && communicationType != null && clientId != -1 && username != null) {
            User(
                client_id = clientId,
                document_type = documentType,
                user_role = userRole,
                id_number = userId,
                name = userName,
                last_name = userLastName ?: "",
                email = userEmail,
                cellphone = "",
                communication_type = communicationType,
                username = username,  // Asignar username al crear el usuario
                id = -1,  // Puedes cambiarlo según sea necesario si tienes un campo ID
                cognito_user_sub = "" // Cambia esto según sea necesario
            )
        } else {
            Log.e("ProfileFragment", "No se encontró usuario en SharedPreferences")
            null
        }
    }

    fun saveIdToken(idToken: String) {
        val editor = preferences.edit()
        editor.putString("ID_TOKEN", idToken)
        editor.apply()
    }

    fun getIdToken(): String? {
        return preferences.getString("ID_TOKEN", null)
    }


    // Eliminar datos de usuario
    fun clearUserData() {
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }
}
