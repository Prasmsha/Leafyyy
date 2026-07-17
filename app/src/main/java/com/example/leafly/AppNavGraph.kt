package com.example.leafly

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.leafly.Repo.CareTaskRepoImp
import com.example.leafly.Repo.GrowthLogRepoImp
import com.example.leafly.Repo.PlantRepoImp
import com.example.leafly.Repo.UserRepoImp
import com.example.leafly.ViewModel.CareTaskViewModel
import com.example.leafly.ViewModel.CareTaskViewModelFactory
import com.example.leafly.ViewModel.GrowthLogViewModel
import com.example.leafly.ViewModel.GrowthLogViewModelFactory
import com.example.leafly.ViewModel.PlantViewModel
import com.example.leafly.ViewModel.PlantViewModelFactory
import com.example.leafly.ViewModel.UserViewModel
import com.example.leafly.ViewModel.UserViewModelFactory

@Composable
fun AppNavGraph(
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {
    val navController = rememberNavController()
    val userViewModel: UserViewModel = viewModel(factory = UserViewModelFactory(UserRepoImp()))
    val plantViewModel: PlantViewModel = viewModel(factory = PlantViewModelFactory(PlantRepoImp()))
    val careTaskViewModel: CareTaskViewModel = viewModel(factory = CareTaskViewModelFactory(CareTaskRepoImp()))
    val growthLogViewModel: GrowthLogViewModel = viewModel(factory = GrowthLogViewModelFactory(GrowthLogRepoImp()))

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onGetStarted = { navController.navigate("register") { popUpTo("splash") { inclusive = true } } },
                onAutoLogin = { navController.navigate("dashboard") { popUpTo("splash") { inclusive = true } } },
                onAlreadyHaveAccount = { navController.navigate("login") { popUpTo("splash") { inclusive = true } } }
            )
        }
        composable("login") {
            LoginScreen(
                userViewModel = userViewModel,
                onSignInClick = { navController.navigate("dashboard") { popUpTo(0) { inclusive = true } } },
                onSignUpClick = { navController.navigate("register") },
                onForgotPasswordClick = { navController.navigate("forgotPassword") }
            )
        }
        composable("register") {
            RegisterScreen(
                userViewModel = userViewModel,
                onRegisterClick = { navController.navigate("dashboard") { popUpTo(0) { inclusive = true } } },
                onSignInClick = { navController.navigate("login") }
            )
        }
        composable("forgotPassword") {
            ForgotPasswordScreen(
                userViewModel = userViewModel,
                onBackClick = { navController.popBackStack() },
                onSignInClick = { navController.navigate("login") }
            )
        }
        composable("dashboard") {
            Dashboard(
                userViewModel = userViewModel,
                plantViewModel = plantViewModel,
                onPlantsClick = { navController.navigate("plantList") },
                onProfileClick = { navController.navigate("profile") },
                onAlertsClick = { navController.navigate("reminders") },
                onExportClick = { navController.navigate("exportData") },
                onSavedTipsClick = { navController.navigate("savedTips") }
            )
        }
        composable("profile") {
            ProfileScreen(
                userViewModel = userViewModel,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onBackClick = { navController.popBackStack() },
                onLogoutClick = { navController.navigate("splash") { popUpTo(0) { inclusive = true } } }
            )
        }
        composable("reminders") {
            RemindersScreen(
                plantViewModel = plantViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("plantList") {
            PlantListScreen(
                plantViewModel = plantViewModel,
                onAddPlantClick = { navController.navigate("addPlant") },
                onPlantClick = { plantId -> navController.navigate("plantDetail/$plantId") }
            )
        }
        composable("addPlant") {
            AddPlantScreen(
                plantViewModel = plantViewModel,
                onBackClick = { navController.popBackStack() },
                onPlantAdded = { navController.popBackStack() }
            )
        }
        composable("plantDetail/{plantId}") { backStackEntry ->
            val plantId = backStackEntry.arguments?.getString("plantId") ?: ""
            PlantDetailScreen(
                plantId = plantId,
                plantViewModel = plantViewModel,
                careTaskViewModel = careTaskViewModel,
                onBackClick = { navController.popBackStack() },
                onEditClick = { navController.navigate("editPlant/$plantId") },
                onGrowthLogClick = {
                    val plantName = java.net.URLEncoder.encode(plantViewModel.selectedPlant.value?.name ?: "", "UTF-8")
                    navController.navigate("growthLog/$plantId/$plantName")
                },
                onAiClick = {
                    val plant = plantViewModel.selectedPlant.value
                    val plantName = java.net.URLEncoder.encode(plant?.name ?: "Unknown", "UTF-8")
                    val plantSpecies = java.net.URLEncoder.encode(plant?.species ?: "Unknown", "UTF-8")
                    navController.navigate("aiPlantCare/$plantId/$plantName/$plantSpecies")
                }
            )
        }
        composable("editPlant/{plantId}") { backStackEntry ->
            val plantId = backStackEntry.arguments?.getString("plantId") ?: ""
            EditPlantScreen(
                plantId = plantId,
                plantViewModel = plantViewModel,
                onBackClick = { navController.popBackStack() },
                onPlantUpdated = { navController.popBackStack() }
            )
        }
        composable("growthLog/{plantId}/{plantName}") { backStackEntry ->
            val plantId = backStackEntry.arguments?.getString("plantId") ?: ""
            val plantName = backStackEntry.arguments?.getString("plantName") ?: ""
            GrowthLogScreen(
                plantId = plantId,
                plantName = plantName,
                growthLogViewModel = growthLogViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("aiPlantCare/{plantId}/{plantName}/{plantSpecies}") { backStackEntry ->
            val plantName = backStackEntry.arguments?.getString("plantName") ?: ""
            val plantSpecies = backStackEntry.arguments?.getString("plantSpecies") ?: ""
            AiPlantCareScreen(
                plantName = plantName,
                plantSpecies = plantSpecies,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("exportData") {
            ExportDataScreen(plantViewModel = plantViewModel, onBackClick = { navController.popBackStack() })
        }
        composable("savedTips") {
            SavedTipsScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
