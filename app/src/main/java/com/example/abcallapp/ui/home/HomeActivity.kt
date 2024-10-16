package com.example.abcallapp.ui.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.abcallapp.R
import com.example.abcallapp.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)  // Configura el Toolbar como la ActionBar

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_home)

        // Configurar las rutas de navegación
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_chatbot, R.id.navigation_notifications)
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Manejo de la navegación inferior
        navView.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    navController.navigate(R.id.navigation_home) // Navegar a HomeFragment
                    true
                }
                R.id.navigation_chatbot -> {
                    navController.navigate(R.id.navigation_chatbot) // Navegar a ChatbotFragment
                    true
                }
                R.id.navigation_notifications -> {
                    navController.navigate(R.id.navigation_notifications) // Navegar a NotificationsFragment
                    true
                }
                else -> false
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Esto mostrará la flecha

        // Acción para navegar al fragmento de perfil desde el TopBar
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_perfil -> {
                    navController.navigate(R.id.profileFragment) // Navega al fragmento de perfil
                    true
                }
                else -> false
            }
        }

        // Acción de navegación al presionar el ícono de "Atrás"
        binding.topAppBar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

    }

    // Cargar el menú de opciones en la barra superior
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.custom_menu, menu)  // Infla el menú personalizado
        return true
    }

    // Manejo del botón "Atrás" en la barra superior
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
