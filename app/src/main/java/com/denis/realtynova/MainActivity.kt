package com.denis.realtynova

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.denis.realtynova.core.designsystem.components.BottomTab
import com.denis.realtynova.core.designsystem.components.CreativeBottomBar
import com.denis.realtynova.core.designsystem.theme.REALTYNOVATheme
import com.denis.realtynova.core.navigation.RealtyNovaNavHost
import com.denis.realtynova.core.navigation.Route
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            REALTYNOVATheme {
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
                    !dest.hasRoute<Route.Register>()
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
        }
    }
}
