package com.autolauncher

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.autolauncher.ui.home.HomeScreen
import com.autolauncher.ui.home.HomeViewModel
import com.autolauncher.ui.theme.AutoLauncherTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // El launcher nunca debe cerrarse con el botón Atrás
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = Unit
        })

        enableEdgeToEdge()

        setContent {
            val app = application as AutoLauncherApp
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(app.prefs, app.appRepository, app.vehicleRepository)
            )
            val uiState by viewModel.uiState.collectAsState()

            AutoLauncherTheme(seedColor = uiState.interfaceColor) {
                HomeScreen(
                    uiState = uiState,
                    onEvent = viewModel::onEvent
                )
            }
        }
    }

    // Cuando se presiona el botón HOME estando ya en el launcher, cerramos paneles abiertos
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Se maneja en el ViewModel a través del flag de HomeIntent
    }
}
