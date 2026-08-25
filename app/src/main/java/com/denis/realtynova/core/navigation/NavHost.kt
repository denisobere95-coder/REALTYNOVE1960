package com.denis.realtynova.core.navigation

import android.app.Activity
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.navigation.navDeepLink
import com.denis.realtynova.features.auth.*
import com.denis.realtynova.features.booking.BookingScreen
import com.denis.realtynova.features.dashboard.*
import com.denis.realtynova.features.home.*
import com.denis.realtynova.features.map.MapScreen
import com.denis.realtynova.features.messages.ChatDetailScreen
import com.denis.realtynova.features.messages.MessagesScreen
import com.denis.realtynova.features.payment.PaymentScreen
import com.denis.realtynova.features.profile.EditProfileScreen
import com.denis.realtynova.features.profile.ProfileScreen
import com.denis.realtynova.features.saved.SavedScreen
import com.denis.realtynova.features.search.*

@Composable
fun RealtyNovaNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // Shared Auth ViewModel for the entire auth flow
    val authViewModel: AuthViewModel = hiltViewModel()

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
            SplashScreen(
                viewModel = authViewModel,
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
            OnboardingScreen(
                viewModel = authViewModel,
                onNavigateToAuth = {
                    navController.navigate(Route.Welcome) {
                        popUpTo(Route.Onboarding) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Welcome> {
            val currentUser by authViewModel.currentUser.collectAsState()
            
            LaunchedEffect(currentUser) {
                if (currentUser != null) {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Welcome) { inclusive = true }
                    }
                }
            }

            WelcomeScreen(
                onNavigateToLogin = { navController.navigate(Route.Login) },
                onNavigateToRegister = { navController.navigate(Route.Register) }
            )
        }

        composable<Route.Login> {
            val context = LocalContext.current
            val uiState by authViewModel.uiState.collectAsState()

            LoginScreen(
                onLoginSuccessAction = { email, password ->
                    authViewModel.login(email, password)
                },
                onRegisterClickAction = {
                    navController.navigate(Route.Register) {
                        launchSingleTop = true
                    }
                },
                onForgotPasswordAction = { email ->
                    authViewModel.resetPassword(email)
                },
                onGoogleSignInAction = {
                    authViewModel.signInWithGoogle(context)
                },
                onPhoneAuthClickAction = {
                    navController.navigate(Route.PhoneLogin)
                },
                onBackClickAction = {
                    navController.popBackStack()
                },
                isLoading = uiState is AuthUiState.Loading,
                errorMessage = (uiState as? AuthUiState.Error)?.message,
                successMessage = (uiState as? AuthUiState.SuccessMessage)?.message
            )

            LaunchedEffect(uiState) {
                if (uiState is AuthUiState.Success) {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Welcome) { inclusive = true }
                    }
                }
            }
        }

        composable<Route.Register> {
            val context = LocalContext.current
            val uiState by authViewModel.uiState.collectAsState()

            RegisterScreen(
                onRegisterSuccessAction = { name, email, password, phone, role ->
                    authViewModel.signUp(name, email, password, phone, com.denis.realtynova.core.domain.model.UserRole.valueOf(role))
                },
                onLoginClickAction = {
                    navController.navigate(Route.Login) {
                        launchSingleTop = true
                    }
                },
                onGoogleSignInAction = {
                    authViewModel.signInWithGoogle(context)
                },
                onPhoneAuthClickAction = {
                    navController.navigate(Route.PhoneLogin)
                },
                onBackClickAction = {
                    navController.popBackStack()
                },
                isLoading = uiState is AuthUiState.Loading,
                errorMessage = (uiState as? AuthUiState.Error)?.message
            )

            LaunchedEffect(uiState) {
                if (uiState is AuthUiState.Success) {
                    navController.navigate(Route.AccountType)
                }
            }
        }

        composable<Route.PhoneLogin> {
            val uiState by authViewModel.uiState.collectAsState()

            PhoneLoginScreen(
                onSendCodeAction = { activity, phone ->
                    authViewModel.sendOtpCode(activity, phone)
                },
                onBackClickAction = { navController.popBackStack() },
                isSending = uiState is AuthUiState.Loading,
                errorMessage = (uiState as? AuthUiState.Error)?.message
            )

            LaunchedEffect(uiState) {
                if (uiState is AuthUiState.CodeSent) {
                    val phone = (uiState as AuthUiState.CodeSent).phoneNumber
                    navController.navigate(Route.Otp(phone))
                }
            }
        }

        composable<Route.Otp> { backStackEntry ->
            val route: Route.Otp = backStackEntry.toRoute()
            val uiState by authViewModel.uiState.collectAsState()
            val context = LocalContext.current
            val activity = context as? Activity

            OtpScreen(
                phoneNumber = route.phoneNumber,
                onVerify = { code ->
                    authViewModel.verifyOtp(route.phoneNumber, code)
                },
                onResend = {
                    if (activity != null) {
                        authViewModel.resendOtpCode(activity, route.phoneNumber)
                    }
                },
                onNavigateBack = { navController.popBackStack() },
                isVerifying = uiState is AuthUiState.Loading,
                errorMessage = (uiState as? AuthUiState.Error)?.message
            )
            
            LaunchedEffect(uiState) {
                if (uiState is AuthUiState.Success) {
                    navController.navigate(Route.AccountType)
                }
            }
        }

        composable<Route.AccountType> {
            AccountTypeScreen(
                onTypeSelected = { role ->
                    authViewModel.setUserRole(role)
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
                    navController.navigate(Route.Notifications)
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
                },
                onNavigateToCountyExplorer = {
                    navController.navigate(Route.CountyExplorer)
                },
                onNavigateToMatchmaker = {
                    navController.navigate(Route.Matchmaker)
                },
                onNavigateToMarketInsights = {
                    navController.navigate(Route.MarketInsights)
                }
            )
        }
        composable<Route.Search>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "realtynova://search" }
            )
        ) { 
            SearchScreen(
                onPropertyClick = { id -> navController.navigate(Route.PropertyDetail(id)) },
                onOpenAi = { navController.navigate(Route.AiAssistant) },
                onFiltersClick = { /* Show filters sheet */ },
                onOpenMap = { navController.navigate(Route.Map) }
            )
        }
        composable<Route.Map> {
            MapScreen(
                onNavigateToDetail = { id ->
                    navController.navigate(Route.PropertyDetail(id))
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToSearch = {
                    navController.navigate(Route.Search)
                }
            )
        }
        composable<Route.Saved> { 
            SavedScreen(
                onPropertyClick = { id -> navController.navigate(Route.PropertyDetail(id)) },
                onCompareClick = { id1, id2 -> navController.navigate(Route.PropertyComparison(id1, id2)) },
                onExploreProperties = { navController.navigate(Route.Home) },
                onSearch = { navController.navigate(Route.Search) },
                onNotifications = { navController.navigate(Route.Notifications) }
            )
        }
        composable<Route.Profile> { 
            ProfileScreen(
                onNavigateToEditProfile = { navController.navigate(Route.EditProfile) },
                onNavigateToMessages = { navController.navigate(Route.Messages) },
                onNavigateToAdminDashboard = { navController.navigate(Route.AdminDashboard) },
                onNavigateToAgentDashboard = { navController.navigate(Route.AgentDashboard) },
                onNavigateToNotifications = { navController.navigate(Route.Notifications) },
                onNavigateToMortgageCalculator = { navController.navigate(Route.MortgageCalculator) },
                onLogout = {
                    navController.navigate(Route.Welcome) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.PropertyDetail>(
            deepLinks = listOf(
                navDeepLink { uriPattern = Route.PropertyDetail.DEEP_LINK_URI }
            )
        ) { backStackEntry ->
            val route: Route.PropertyDetail = backStackEntry.toRoute()
            PropertyDetailScreen(
                id = route.id,
                onBack = { navController.popBackStack() },
                onNavigateToBooking = { propertyId ->
                    navController.navigate(Route.Booking(propertyId))
                },
                onNavigateToPayment = { propertyId, amount ->
                    navController.navigate(Route.Payment(propertyId, amount))
                },
                onNavigateToVirtualTour = { propertyId ->
                    navController.navigate(Route.VirtualTour(propertyId))
                }
            )
        }

        composable<Route.AiAssistant>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "realtynova://ai" }
            )
        ) {
            AiAssistantScreen(
                onBack = { navController.popBackStack() },
                onPropertyClick = { id ->
                    navController.navigate(Route.PropertyDetail(id))
                }
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

        composable<Route.VirtualTour> { backStackEntry ->
            val route: Route.VirtualTour = backStackEntry.toRoute()
            VirtualTourScreen(
                propertyId = route.propertyId,
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Notifications> {
            NotificationsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.CountyExplorer> {
            CountyExplorerScreen(
                onBack = { navController.popBackStack() },
                onCountyClick = { _ ->
                    navController.navigate(Route.Search)
                }
            )
        }

        composable<Route.MarketInsights> {
            MarketInsightsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.MortgageCalculator> {
            MortgageCalculatorScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable<Route.Matchmaker> {
            MatchmakerScreen(
                onBack = { navController.popBackStack() },
                onPropertyClick = { id ->
                    navController.navigate(Route.PropertyDetail(id))
                }
            )
        }

        composable<Route.PropertyComparison> { backStackEntry ->
            val route: Route.PropertyComparison = backStackEntry.toRoute()
            PropertyComparisonScreen(
                id1 = route.propertyId1,
                id2 = route.propertyId2,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
