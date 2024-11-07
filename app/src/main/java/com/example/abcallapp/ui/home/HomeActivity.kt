package com.example.abcallapp.ui.home

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.abcallapp.R
import com.example.abcallapp.databinding.ActivityHomeBinding
import com.google.android.material.appbar.MaterialToolbar

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val profileImages = listOf(
        R.drawable.usuariochat2,
        R.drawable.usuariochat3,
        R.drawable.usuariochat4,
        R.drawable.usuariochat5,
        R.drawable.usuariochat6
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)  // Configura el Toolbar como la ActionBar

        //val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_home)

        // Configurar las rutas de navegación
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home)
        )
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> {
                    // Oculta la flecha de regreso en el fragmento Home
                    binding.topAppBar.navigationIcon = null
                }
                else -> {
                    // Muestra la flecha de regreso en otros fragmentos
                    binding.topAppBar.setNavigationIcon(R.drawable.baseline_arrow_back_24)
                    binding.topAppBar.setNavigationOnClickListener {
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)

        // Manejo de la navegación inferior
        binding.navView.setOnItemSelectedListener { item: MenuItem ->
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
        // supportActionBar?.setDisplayHomeAsUpEnabled(true)  // Esto mostrará la flecha

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
        // Seleccionar una imagen aleatoria y establecerla en el ícono de perfil
        val randomImage = profileImages.random()
        val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putInt("user_profile_image", randomImage).apply()
        menu?.findItem(R.id.action_perfil)?.icon = ContextCompat.getDrawable(this, randomImage)

        return true
    }

    // Manejo del botón "Atrás" en la barra superior
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
