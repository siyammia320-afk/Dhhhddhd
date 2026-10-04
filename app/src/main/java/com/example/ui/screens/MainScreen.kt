package com.example.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.viewmodel.SecureViewModel

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Portal : Screen("portal", "Portal", Icons.Default.Public)
    object Vault : Screen("vault", "Vault", Icons.Default.Key)
    object Tools : Screen("tools", "Tools", Icons.Default.Build)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: SecureViewModel) {
    var selectedScreen by remember { mutableStateOf<Screen>(Screen.Portal) }
    val items = listOf(Screen.Portal, Screen.Vault, Screen.Tools, Screen.Settings)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = selectedScreen == screen,
                        onClick = { selectedScreen = screen }
                    )
                }
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (selectedScreen) {
                is Screen.Portal -> PortalScreen(viewModel)
                is Screen.Vault -> VaultScreen(viewModel)
                is Screen.Tools -> ToolsScreen(viewModel)
                is Screen.Settings -> SettingsScreen(viewModel)
            }
        }
    }
}
