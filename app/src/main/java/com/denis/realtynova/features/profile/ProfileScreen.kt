package com.denis.realtynova.features.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.domain.model.UserRole
import com.denis.realtynova.features.auth.AuthViewModel

@Composable
fun ProfileScreen(
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToMessages: () -> Unit = {},
    onNavigateToAdminDashboard: () -> Unit = {},
    onNavigateToAgentDashboard: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToMortgageCalculator: () -> Unit = {},
    onLogout: () -> Unit = {},
    viewModel: AuthViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()
    val role by viewModel.userRole.collectAsState()

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Spacer(modifier = Modifier.height(100.dp))
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(bottom = 120.dp)
        ) {
            ProfileHero(
                name = user?.displayName ?: "Elite Member",
                email = user?.email ?: "member@realtynova.com",
                photoUrl = user?.photoUrl,
                role = role,
                onEditClick = onNavigateToEditProfile
            )

            Spacer(modifier = Modifier.height(24.dp))

            ProfileSection(title = "MANAGEMENT") {
                ProfileMenuItem(
                    icon = Icons.Default.PersonOutline,
                    title = "Personal Information",
                    onClick = onNavigateToEditProfile
                )
                ProfileMenuItem(
                    icon = Icons.Default.ChatBubbleOutline,
                    title = "Messages",
                    onClick = onNavigateToMessages
                )
                ProfileMenuItem(
                    icon = Icons.Default.VerifiedUser,
                    title = "Identity Verification",
                    subtitle = "Get your blue badge",
                    onClick = {}
                )
                ProfileMenuItem(
                    icon = Icons.Default.Calculate,
                    title = "Mortgage Calculator",
                    onClick = onNavigateToMortgageCalculator
                )
                
                if (role == UserRole.ADMIN) {
                    ProfileMenuItem(
                        icon = Icons.Default.AdminPanelSettings,
                        title = "Admin Dashboard",
                        onClick = onNavigateToAdminDashboard
                    )
                }
                
                if (role == UserRole.AGENT || role == UserRole.ADMIN) {
                    ProfileMenuItem(
                        icon = Icons.Default.Dashboard,
                        title = "Agent Dashboard",
                        onClick = onNavigateToAgentDashboard
                    )
                }

                ProfileMenuItem(
                    icon = Icons.Default.PrivacyTip,
                    title = "Security & Privacy",
                    onClick = onNavigateToEditProfile
                )
            }

            ProfileSection(title = "PREFERENCES") {
                ProfileMenuItem(
                    icon = Icons.Default.NotificationsNone,
                    title = "Notification Settings",
                    onClick = onNavigateToNotifications
                )
                ProfileMenuItem(
                    icon = Icons.Default.Language,
                    title = "Language",
                    subtitle = "English (US)",
                    onClick = {}
                )
            }

            ProfileSection(title = "SUPPORT") {
                ProfileMenuItem(
                    icon = Icons.Default.HeadsetMic,
                    title = "Live AI Concierge",
                    subtitle = "Always active",
                    onClick = {}
                )
                ProfileMenuItem(
                    icon = Icons.Default.HelpOutline,
                    title = "Help Center",
                    onClick = {}
                )
                ProfileMenuItem(
                    icon = Icons.Default.Info,
                    title = "About REALTYNOVA",
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clickable(onClick = {
                        viewModel.logout()
                        onLogout()
                    }),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "LOGOUT",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHero(
    name: String,
    email: String,
    photoUrl: String?,
    role: UserRole,
    onEditClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DeepEmerald, MaterialTheme.colorScheme.background)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier
                        .size(100.dp)
                        .border(4.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    if (photoUrl != null) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = name.take(1).uppercase(),
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepEmerald
                            )
                        }
                    }
                }
                
                Surface(
                    modifier = Modifier.size(32.dp).clickable { onEditClick() },
                    shape = CircleShape,
                    color = ChampagneGold,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change photo",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = if (photoUrl == null) MaterialTheme.colorScheme.onBackground else Color.White
            )
            
            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = if (photoUrl == null) MaterialTheme.colorScheme.onSurfaceVariant else Color.White.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                shape = RoundedCornerShape(50.dp),
                color = ChampagneGold.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(1.dp, ChampagneGold)
            ) {
                Text(
                    text = role.name,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                    color = ChampagneGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun ProfileSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = DeepEmerald
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.outline
        )
    }
}
