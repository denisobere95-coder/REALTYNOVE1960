package com.denis.realtynova

import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.denis.realtynova.core.data.manager.SessionManager
import com.denis.realtynova.core.designsystem.components.BottomTab
import com.denis.realtynova.core.designsystem.components.CreativeBottomBar
import com.denis.realtynova.core.designsystem.components.RealtyNovaButton
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.REALTYNOVATheme
import com.denis.realtynova.core.navigation.RealtyNovaNavHost
import com.denis.realtynova.core.navigation.Route
import com.denis.realtynova.core.util.BiometricManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject lateinit var sessionManager: SessionManager
    @Inject lateinit var biometricManager: BiometricManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Test Crash Button
        val crashButton = Button(this)
        crashButton.text = "Test Crash"
        crashButton.setOnClickListener {
           throw RuntimeException("Test Crash") // Force a crash
        }
        addContentView(crashButton, ViewGroup.LayoutParams(
               ViewGroup.LayoutParams.MATCH_PARENT,
               ViewGroup.LayoutParams.WRAP_CONTENT))

        setContent {
            REALTYNOVATheme {
                val isBiometricEnabled by sessionManager.isBiometricEnabled.collectAsState(initial = false)
                val isScreenshotPreventionEnabled by sessionManager.isScreenshotPreventionEnabled.collectAsState(initial = false)

                var isUnlocked by remember { mutableStateOf(false) }
                val scope = rememberCoroutineScope()

                LaunchedEffect(isScreenshotPreventionEnabled) {
                    if (isScreenshotPreventionEnabled) {
                        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
                    } else {
                        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
                    }
                }

                if (isBiometricEnabled && !isUnlocked) {
                    val snackbarHostState = remember { SnackbarHostState() }
                    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
                        BiometricLockScreen(
                            onAuthRequested = {
                                biometricManager.authenticate(
                                    activity = this,
                                    onSuccess = { isUnlocked = true },
                                    onError = { error ->
                                        scope.launch {
                                            snackbarHostState.showSnackbar(error)
                                        }
                                    }
                                )
                            },
                            modifier = Modifier.padding(padding)
                        )
                    }
                } else {
                    MainContent()
                }
            }
        }
    }

    @Composable
    private fun MainContent() {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        val tabs = remember {
            listOf(
                BottomTab(Route.Home, Icons.Default.Home, "Home"),
                BottomTab(Route.Search, Icons.Default.Search, "Search"),
                BottomTab(Route.Map, Icons.Default.LocationOn, "Map"),
                BottomTab(Route.Saved, Icons.Default.Favorite, "Saved"),
                BottomTab(Route.Profile, Icons.Default.Person, "Profile"),
            )
        }

        val showBottomBar = currentDestination?.let { dest ->
            !dest.hasRoute<Route.Splash>() &&
            !dest.hasRoute<Route.Welcome>() &&
            !dest.hasRoute<Route.Login>() &&
            !dest.hasRoute<Route.Register>() &&
            !dest.hasRoute<Route.Onboarding>() &&
            !dest.hasRoute<Route.PhoneLogin>() &&
            !dest.hasRoute<Route.Otp>() &&
            !dest.hasRoute<Route.AccountType>()
        } ?: false

        val selectedIndex = when {
            currentDestination?.hasRoute<Route.Home>() == true -> 0
            currentDestination?.hasRoute<Route.Search>() == true -> 1
            currentDestination?.hasRoute<Route.Map>() == true -> 2
            currentDestination?.hasRoute<Route.Saved>() == true -> 3
            currentDestination?.hasRoute<Route.Profile>() == true -> 4
            else -> 0
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                if (showBottomBar) {
                    CreativeBottomBar(
                        tabs = tabs,
                        selectedTabIndex = selectedIndex,
                        onTabSelected = { index ->
                            val route = tabs[index].route
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            RealtyNovaNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    @Composable
    private fun BiometricLockScreen(onAuthRequested: () -> Unit, modifier: Modifier = Modifier) {
        LaunchedEffect(Unit) {
            onAuthRequested()
        }

        Box(modifier = modifier.fillMaxSize().background(DeepEmerald), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "REALTYNOVA LOCKED", color = Color.White, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Text(text = "Secure biometric authentication required", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Spacer(modifier = Modifier.height(48.dp))
                RealtyNovaButton(onClick = onAuthRequested) {
                    Text("TRY AGAIN", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
