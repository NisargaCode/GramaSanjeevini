package com.example.gramasanjeevini.ui

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Selection : Screen("selection")
    object Login : Screen("login/{userType}") {
        fun createRoute(userType: String) = "login/$userType"
    }
    object Home : Screen("home")
    object Emergency : Screen("emergency")
    object PharmacistDashboard : Screen("pharmacist_dashboard/{shopId}") {
        fun createRoute(shopId: String) = "pharmacist_dashboard/$shopId"
    }
    object Profile : Screen("profile")
}
