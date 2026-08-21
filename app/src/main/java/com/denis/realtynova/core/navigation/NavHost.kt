package com.denis.realtynova.core.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.denis.realtynova.features.auth.*
import com.denis.realtynova.features.booking.BookingScreen
import com.denis.realtynova.features.dashboard.*
import com.denis.realtynova.features.home.HomeScreen
import com.denis.realtynova.features.home.PropertyDetailScreen
import com.denis.realtynova.features.map.MapScreen
import com.denis.realtynova.features.messages.ChatDetailScreen
import com.denis.realtynova.features.messages.MessagesScreen
import com.denis.realtynova.features.payment.PaymentScreen
import com.denis.realtynova.features.profile.EditProfileScreen
import com.denis.realtynova.features.profile.ProfileScreen
import com.denis.realtynova.features.saved.SavedScreen
import com.denis.realtynova.features.search.AiAssistantScreen
import com.denis.realtynova.features.search.SearchScreen

@Composable
fun RealtyNovaNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.Splash,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.95f, animationSpec = tween(300))
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.95f, animationSpec = tween(300))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 1.05f, animationSpec = tween(300))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 1.05f, animationSpec = tween(300))
        }
    ) {
        composable<Route.Splash> {
            val viewModel: AuthViewModel = hiltViewModel()
            SplashScreen(
                viewModel = viewModel,
                onNavigateToMain = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                },
                onNavigateToWelcome = {
                    navController.navigate(Route.Welcome) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(Route.Onboarding) {
                        popUpTo(Route.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Onboarding> {
            val viewModel: AuthViewModel = hiltViewModel()
            OnboardingScreen(
                viewModel = viewModel,
                onNavigateToAuth = {
                    navController.navigate(Route.Welcome) {
                        popUpTo(Route.Onboarding) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Welcome> {
            WelcomeScreen(
                onNavigateToLogin = { navController.navigate(Route.Login) },
                onNavigateToRegister = { navController.navigate(Route.Register) }
            )
        }

        composable<Route.Login> {
            LoginScreen(
                onLoginSuccessAction = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Welcome) { inclusive = true }
                    }
                },
                onRegisterClickAction = {
                    navController.navigate(Route.Register) {
                        launchSingleTop = true
                    }
                },
                onPhoneAuthClickAction = {
                    navController.navigate(Route.PhoneLogin)
                },
                onBackClickAction = {
                    navController.popBackStack()
                }
            )
        }

        composable<Route.Register> {
            RegisterScreen(
                onRegisterSuccessAction = {
                    navController.navigate(Route.Otp("your-phone"))
                },
                onLoginClickAction = {
                    navController.navigate(Route.Login) {
                        launchSingleTop = true
                    }
                },
                onPhoneAuthClickAction = {
                    navController.navigate(Route.PhoneLogin)
                },
                onBackClickAction = {
                    navController.popBackStack()
                }
            )
        }

        composable<Route.PhoneLogin> {
            val viewModel: AuthViewModel = hiltViewModel()
            PhoneLoginScreen(
                onSendCodeAction = { phone ->
                    viewModel.setVerificationId("dummy-id")
                    navController.navigate(Route.Otp(phone))
                },
                onBackClickAction = { navController.popBackStack() }
            )
        }

        composable<Route.Otp> { backStackEntry ->
            val route: Route.Otp = backStackEntry.toRoute()
            val viewModel: AuthViewModel = hiltViewModel()
            OtpScreen(
                phoneNumber = route.phoneNumber,
                onVerify = { otp ->
                    viewModel.signInWithPhone(route.phoneNumber, otp)
                },
                onResend = { /* Logic */ },
                onNavigateBack = { navController.popBackStack() }
            )
            
            val uiState by viewModel.uiState.collectAsState()
            androidx.compose.runtime.LaunchedEffect(uiState) {
                if (uiState is AuthUiState.Success) {
                    navController.navigate(Route.AccountType)
                }
            }
        }

        composable<Route.AccountType> {
            val viewModel: AuthViewModel = hiltViewModel()
            AccountTypeScreen(
                onTypeSelected = { role ->
                    viewModel.setUserRole(role)
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Welcome) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Home> {
            HomeScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(Route.PropertyDetail(id))
                },
                onNavigateToAiAssistant = {
                    navController.navigate(Route.AiAssistant)
                },
                onNavigateToSearch = {
                    navController.navigate(Route.Search)
                },
                onNavigateToNotifications = {
                    // navController.navigate(Route.Notifications)
                },
                onNavigateToMessages = {
                    navController.navigate(Route.Messages)
                },
                onNavigateToProfile = {
                    navController.navigate(Route.Profile)
                },
                onNavigateToAdminDashboard = {
                    navController.navigate(Route.AdminDashboard)
                },
                onNavigateToAgentDashboard = {
                    navController.navigate(Route.AgentDashboard)
                }
            )
        }
        composable<Route.Search> { SearchScreen() }
        composable<Route.Map> { MapScreen() }
        composable<Route.Saved> { SavedScreen() }
        composable<Route.Profile> { 
            ProfileScreen(
                onNavigateToEditProfile = { navController.navigate(Route.EditProfile) },
                onNavigateToMessages = { navController.navigate(Route.Messages) },
                onNavigateToAdminDashboard = { navController.navigate(Route.AdminDashboard) },
                onNavigateToAgentDashboard = { navController.navigate(Route.AgentDashboard) }
            )
        }

        composable<Route.PropertyDetail> { backStackEntry ->
            val route: Route.PropertyDetail = backStackEntry.toRoute()
            PropertyDetailScreen(
                id = route.id,
                onBack = { navController.popBackStack() },
                onNavigateToBooking = { propertyId ->
                    navController.navigate(Route.Booking(propertyId))
                },
                onNavigateToPayment = { propertyId, amount ->
                    navController.navigate(Route.Payment(propertyId, amount))
                }
            )
        }

        composable<Route.AiAssistant> {
            AiAssistantScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Booking> { backStackEntry ->
            val route: Route.Booking = backStackEntry.toRoute()
            BookingScreen(
                propertyId = route.propertyId,
                onBack = { navController.popBackStack() },
                onBookingConfirmed = {
                    navController.popBackStack()
                }
            )
        }

        composable<Route.AdminDashboard> {
            AdminDashboardScreen(
                onBack = { navController.popBackStack() },
                onNavigateToModeration = { navController.navigate(Route.ModerationQueue) }
            )
        }

        composable<Route.AgentDashboard> {
            AgentDashboardScreen(
                onBack = { navController.popBackStack() },
                onManageListings = { navController.navigate(Route.CreateListing) }
            )
        }

        composable<Route.CreateListing> {
            CreateListingScreen(
                onBack = { navController.popBackStack() },
                onSuccess = { navController.popBackStack() }
            )
        }

        composable<Route.ModerationQueue> {
            AdminModerationScreen(
                onNavigateToDetail = { id -> navController.navigate(Route.PropertyDetail(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Messages> {
            MessagesScreen(
                onChatClick = { userId -> navController.navigate(Route.ChatDetail(userId)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.ChatDetail> { backStackEntry ->
            val route: Route.ChatDetail = backStackEntry.toRoute()
            ChatDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Payment> { backStackEntry ->
            val route: Route.Payment = backStackEntry.toRoute()
            PaymentScreen(
                propertyId = route.propertyId,
                amount = route.amount,
                onBack = { navController.popBackStack() },
                onPaymentSuccess = { navController.popBackStack() }
            )
        }

        composable<Route.EditProfile> {
            EditProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
