package com.denis.realtynova.features.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.Graphite
import com.denis.realtynova.core.designsystem.theme.SlateGray
import com.denis.realtynova.core.designsystem.components.CreativeBackground
import com.denis.realtynova.core.designsystem.components.BackgroundVariant
import com.denis.realtynova.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onBack: () -> Unit
) {
    val notifications = listOf(
        NotificationItem(
            id = "1",
            title = "Price Drop Alert!",
            description = "The Skyline Penthouse in Westlands just dropped by KSh 5M.",
            time = "10m ago",
            icon = Icons.Default.NotificationsActive,
            color = Color(0xFFD64545)
        ),
        NotificationItem(
            id = "2",
            title = "Viewing Confirmed",
            description = "Your viewing for Emerald Garden Villa is confirmed for Tomorrow, 10:00 AM.",
            time = "2h ago",
            icon = Icons.Default.CalendarMonth,
            color = DeepEmerald
        ),
        NotificationItem(
            id = "3",
            title = "Account Verified",
            description = "Congratulations! Your identity verification is complete. You now have the verified badge.",
            time = "5h ago",
            icon = Icons.Default.VerifiedUser,
            color = Color(0xFF1877F2)
        )
    )

    CreativeBackground(
        imageRes = R.drawable.img_16,
        variant = BackgroundVariant.EMERALD,
        overlayAlpha = 0.85f
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Notifications", fontWeight = FontWeight.Bold, color = Color.White) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(notifications) { item ->
                    NotificationRow(item)
                }
            }
        }
    }
}

@Composable
private fun NotificationRow(item: NotificationItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.95f),
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(52.dp),
                shape = CircleShape,
                color = item.color.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = null,
                        tint = item.color,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Graphite
                    )
                    Text(
                        text = item.time,
                        fontSize = 11.sp,
                        color = SlateGray,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SlateGray,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

data class NotificationItem(
    val id: String,
    val title: String,
    val description: String,
    val time: String,
    val icon: ImageVector,
    val color: Color
)
