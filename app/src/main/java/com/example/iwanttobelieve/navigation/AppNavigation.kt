package com.example.iwanttobelieve.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.iwanttobelieve.ui.screens.auth.AuthViewModel
import com.example.iwanttobelieve.ui.screens.auth.LoginScreen
import com.example.iwanttobelieve.ui.screens.auth.RegisterScreen
import com.example.iwanttobelieve.ui.screens.feed.FeedScreen
import com.example.iwanttobelieve.ui.screens.post.CreatePostScreen
import com.example.iwanttobelieve.ui.screens.profile.EditProfileScreen
import com.example.iwanttobelieve.ui.screens.profile.ProfileScreen
import com.example.iwanttobelieve.ui.screens.post.EditPostScreen
object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val FEED = "feed"
    const val CREATE_POST = "create_post"
    const val PROFILE = "profile"

    const val EDIT_PROFILE = "edit_profile"
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val startDestination = if (authViewModel.isUserLoggedIn()) Routes.FEED else Routes.LOGIN

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.FEED) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.FEED) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        composable(Routes.FEED) {
            FeedScreen(
                onNavigateToCreatePost = { navController.navigate(Routes.CREATE_POST) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) },
                onNavigateToEditPost = { postId -> navController.navigate("editPost/$postId")}
            )
        }
        composable(Routes.CREATE_POST) {
            CreatePostScreen(onPostCreated = { navController.popBackStack() })
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true } // limpa toda a pilha
                        launchSingleTop = true
                    }
                },
                onNavigateToEditProfile = { navController.navigate(Routes.EDIT_PROFILE) }
            )
        }
        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(onDone = { navController.popBackStack() })
        }
        composable(
            "editPost/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
            EditPostScreen(
                postId = postId,
                onDone = { navController.popBackStack() }
            )
        }

    }
}
