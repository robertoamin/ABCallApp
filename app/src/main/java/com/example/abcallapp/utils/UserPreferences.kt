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
        private const val KEY_ID_NUMBER = "id_number"
        private const val KEY_CELL_PHONE = "cellphone"
        private const val KEY_COGNITO_SUB = "cognito_user_sub"

        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(context: Context): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserPreferences(context).also { INSTANCE = it }
            }
        }
    }

    fun saveUser(user: User) {
        val editor = preferences.edit()

        editor.putInt(KEY_USER_ID, user.id)
        editor.putString(KEY_USER_NAME, user.name)
        editor.putString(KEY_USER_LAST_NAME, user.last_name)
        editor.putString(KEY_USER_EMAIL, user.email)
        editor.putString(KEY_USER_ROLE, user.user_role)
        editor.putString(KEY_DOCUMENT_TYPE, user.document_type)
        editor.putString(KEY_COMMUNICATION_TYPE, user.communication_type)
        editor.putInt(KEY_CLIENT_ID, user.client_id)
        editor.putString(KEY_USERNAME, user.username)
        editor.putString(KEY_ID_NUMBER, user.id_number)
        editor.putString(KEY_CELL_PHONE, user.cellphone) // Almacena como String
        editor.putString(KEY_COGNITO_SUB, user.cognito_user_sub)
        editor.apply()

        Log.d("UserPreferences", "Usuario guardado: ${user.name} ${user.last_name}, Email: ${user.email}")
    }


    // Leer información del usuario
    fun getUser(): User? {
        val userId = preferences.getInt(KEY_USER_ID, -1)
        val userName = preferences.getString(KEY_USER_NAME, null)
        val userLastName = preferences.getString(KEY_USER_LAST_NAME, null)
        val userEmail = preferences.getString(KEY_USER_EMAIL, null)
        val userRole = preferences.getString(KEY_USER_ROLE, null)
        val documentType = preferences.getString(KEY_DOCUMENT_TYPE, null)
        val communicationType = preferences.getString(KEY_COMMUNICATION_TYPE, null)
        val clientId = preferences.getInt(KEY_CLIENT_ID, -1)
        val username = preferences.getString(KEY_USERNAME, null)
        val idNumber = preferences.getString(KEY_ID_NUMBER, null)
        val cellphone = preferences.getString(KEY_CELL_PHONE, "")
        val cognitoSub = preferences.getString(KEY_COGNITO_SUB, null)

        Log.d("UserPreferences", "Intentando recuperar datos del usuario: $userName $userLastName $userEmail")
        Log.d("UserPreferences", "userId: $userId, userName: $userName, userLastName: $userLastName, userEmail: $userEmail")
        Log.d("UserPreferences", "userRole: $userRole, documentType: $documentType, communicationType: $communicationType")
        Log.d("UserPreferences", "clientId: $clientId, username: $username, idNumber: $idNumber, cellphone: $cellphone, cognitoSub: $cognitoSub")

        return if (userId != -1 && userName != null && userEmail != null && userRole != null && documentType != null && communicationType != null && clientId != -1 && username != null && idNumber != null && cognitoSub != null) {
            User(
                id = userId,
                name = userName,
                last_name = userLastName ?: "",
                email = userEmail,
                user_role = userRole,
                document_type = documentType,
                communication_type = communicationType,
                client_id = clientId,
                username = username,
                id_number = idNumber,
                cellphone = cellphone ?: "",  // Ya está como String
                cognito_user_sub = cognitoSub
            )
        } else {
            Log.e("UserPreferences", "No se encontró usuario en SharedPreferences")
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

