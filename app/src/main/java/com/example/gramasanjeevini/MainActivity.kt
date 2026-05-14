package com.example.gramasanjeevini

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.gramasanjeevini.ui.Screen
import com.example.gramasanjeevini.ui.screens.*
import com.example.gramasanjeevini.ui.theme.GramaSanjeeviniTheme
import com.example.gramasanjeevini.viewmodel.MedicineViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = application as GramaSanjeeviniApplication
            val viewModel: MedicineViewModel = viewModel(
                factory = MedicineViewModel.provideFactory(app.repository)
            )
            
            GramaSanjeeviniTheme {
                MainApp(viewModel)
            }
        }
    }
}

@Composable
fun MainApp(viewModel: MedicineViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    val userProfile by viewModel.userProfile.collectAsState()
    val userName = userProfile?.name ?: "User"

    Scaffold(
        bottomBar = {
            if (currentRoute == Screen.Home.route || currentRoute == Screen.Emergency.route) {
                BottomNavigationBar(navController, viewModel)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(onTimeout = {
                    if (userProfile != null) {
                        if (userProfile?.role == "pharmacist") {
                            navController.navigate(Screen.PharmacistDashboard.createRoute("s1")) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    } else {
                        navController.navigate(Screen.Selection.route) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                })
            }
            composable(Screen.Selection.route) {
                UserSelectionScreen(
                    onVillagerSelected = {
                        navController.navigate(Screen.Login.createRoute("villager"))
                    },
                    onPharmacistSelected = {
                        navController.navigate(Screen.Login.createRoute("pharmacist"))
                    }
                )
            }
            composable(
                route = Screen.Login.route,
                arguments = listOf(navArgument("userType") { type = NavType.StringType })
            ) { backStackEntry ->
                val userType = backStackEntry.arguments?.getString("userType") ?: "villager"
                LoginScreen(
                    userType = userType,
                    onLoginSuccess = { id, name ->
                        viewModel.saveUser(name, userType)
                        if (userType == "pharmacist") {
                            navController.navigate(Screen.PharmacistDashboard.createRoute(id ?: "s1")) {
                                popUpTo(Screen.Selection.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Selection.route) { inclusive = true }
                            }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(viewModel = viewModel, userName = userName)
            }
            composable(Screen.Emergency.route) {
                EmergencyScreen(viewModel = viewModel)
            }
            composable(
                route = Screen.PharmacistDashboard.route,
                arguments = listOf(navArgument("shopId") { type = NavType.StringType })
            ) { backStackEntry ->
                val shopId = backStackEntry.arguments?.getString("shopId") ?: ""
                PharmacistDashboard(
                    shopId = shopId, 
                    userName = userName,
                    onLogout = {
                        navController.navigate(Screen.Selection.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, viewModel: MedicineViewModel) {
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == Screen.Home.route,
            onClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Warning, contentDescription = "Emergency") },
            label = { Text("Emergency") },
            selected = currentRoute == Screen.Emergency.route,
            onClick = {
                navController.navigate(Screen.Emergency.route) {
                    launchSingleTop = true
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Logout") },
            label = { Text("Logout") },
            selected = false,
            onClick = {
                viewModel.logout()
                navController.navigate(Screen.Selection.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}
