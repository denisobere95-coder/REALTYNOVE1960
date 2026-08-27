package com.denis.realtynova.features.search

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.denis.realtynova.core.designsystem.components.CreativeBackground
import com.denis.realtynova.core.designsystem.components.BackgroundVariant
import com.denis.realtynova.R
import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.Graphite
import com.denis.realtynova.core.designsystem.theme.RealtyNovaTextStyles
import java.util.Locale

private val LuxuryGold = Color(0xFFD7B56D)
private val AiDark = Color(0xFF10151C)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    onBack: () -> Unit = {},
    onPropertyClick: (String) -> Unit = {},
    viewModel: AiAssistantViewModel = hiltViewModel()
) {
    var query by remember { mutableStateOf("") }
    val messages = viewModel.messages
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    CreativeBackground(
        imageRes = R.drawable.img_21,
        variant = BackgroundVariant.NAVY,
        overlayAlpha = 0.88f
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = { AiTopBar(onBack = onBack) },
            bottomBar = {
                AiInputDock(
                    value = query,
                    onValueChange = { query = it },
                    onSend = {
                        if (query.isNotBlank()) {
                            viewModel.sendMessage(query.trim())
                            query = ""
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (messages.isEmpty()) {
                    item { AiWelcomeHero(onSuggestionClick = { query = it }) }
                }
                items(items = messages) { message ->
                    PremiumChatBubble(
                        message = message,
                        onScheduleViewing = { id -> viewModel.scheduleViewing(id) },
                        onPropertyClick = onPropertyClick
                    )
                }
            }
        }
    }
}

@Composable
private fun AiTopBar(onBack: () -> Unit) {
    Surface(color = AiDark, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Spacer(modifier = Modifier.width(8.dp))
            AiAvatar(size = 40.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "REALTYNOVA AI", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                Text(text = "Luxury Concierge", color = LuxuryGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AiAvatar(size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier.size(size).clip(CircleShape).background(LuxuryGold.copy(alpha = 0.2f)).border(1.dp, LuxuryGold, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = LuxuryGold, modifier = Modifier.size(size * 0.5f))
    }
}

@Composable
private fun AiWelcomeHero(onSuggestionClick: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = AiDark)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = com.denis.realtynova.R.drawable.img_23,
                contentDescription = null,
                modifier = Modifier.fillMaxSize().alpha(0.3f),
                contentScale = ContentScale.Crop
            )
            
            Column(modifier = Modifier.padding(24.dp)) {
                AiAvatar(size = 48.dp)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Elevate your\nproperty journey.", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "I'm your intelligent personal concierge. Tell me your vision, and I'll find the perfect Kenyan residence for you.",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(text = "DISCOVER", style = RealtyNovaTextStyles.HeroEyebrow, color = LuxuryGold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AiSuggestionChip("Villa in Karen under 150M", onSuggestionClick)
                    AiSuggestionChip("Luxury Gated Community", onSuggestionClick)
                    AiSuggestionChip("Sky Residence in Westlands", onSuggestionClick)
                    AiSuggestionChip("Global Investment New York", onSuggestionClick)
                }
            }
        }
    }
}

@Composable
private fun AiSuggestionChip(text: String, onClick: (String) -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick(text) },
        shape = RoundedCornerShape(50.dp),
        color = Color.White.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Text(text = text, color = Color.White, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp))
    }
}

@Composable
private fun PremiumChatBubble(
    message: ChatMessage,
    onScheduleViewing: (String) -> Unit,
    onPropertyClick: (String) -> Unit
) {
    val isUser = message.isUser
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = if (isUser) Alignment.End else Alignment.Start) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.85f),
            color = if (isUser) DeepEmerald else Color.White,
            shape = RoundedCornerShape(
                topStart = 20.dp,
                topEnd = 20.dp,
                bottomStart = if (isUser) 20.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 20.dp
            ),
            shadowElevation = if (isUser) 2.dp else 4.dp,
            border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, LuxuryGold.copy(alpha = 0.2f)) else null
        ) {
            if (message.isTyping) {
                TypingIndicator(modifier = Modifier.padding(16.dp))
            } else {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isUser) Color.White else Graphite,
                    lineHeight = 22.sp
                )
            }
        }
        if (message.properties.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            message.properties.forEach { property ->
                EditorialPropertyCard(property = property, onSchedule = { onScheduleViewing(property.id) }, onClick = { onPropertyClick(property.id) })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun EditorialPropertyCard(property: Property, onSchedule: () -> Unit, onClick: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                val imageUrl = property.images.firstOrNull()?.url ?: ""
                val imageModel = remember(imageUrl) {
                    if (imageUrl.startsWith("res:///drawable/")) {
                        val resName = imageUrl.substringAfterLast("/")
                        context.resources.getIdentifier(resName, "drawable", context.packageName).let {
                            if (it != 0) it else com.denis.realtynova.R.drawable.img_14
                        }
                    } else if (imageUrl.isEmpty()) {
                        com.denis.realtynova.R.drawable.img_14
                    } else {
                        imageUrl
                    }
                }
                AsyncImage(model = imageModel, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                Surface(
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Text(text = property.type.uppercase(), color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = property.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                Text(text = property.location, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "KSh %,.0f".format(property.price), style = RealtyNovaTextStyles.PropertyPrice, color = DeepEmerald)
                    Spacer(modifier = Modifier.weight(1f))
                    if (property.yieldPercentage != null) {
                        Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFE8F5E9)) {
                            Text(text = "Yield: ${property.yieldPercentage}%", color = Color(0xFF2E7D32), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onSchedule, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = DeepEmerald)) {
                    Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SCHEDULE VIEWING", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun TypingIndicator(modifier: Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        repeat(3) { index ->
            val alpha by rememberInfiniteTransition().animateFloat(
                initialValue = 0.2f, targetValue = 1f,
                animationSpec = infiniteRepeatable(animation = tween(600, delayMillis = index * 150), repeatMode = RepeatMode.Reverse),
                label = ""
            )
            Box(modifier = Modifier.size(8.dp).graphicsLayer { this.alpha = alpha }.background(DeepEmerald, CircleShape))
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(text = "Curating...", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun AiInputDock(value: String, onValueChange: (String) -> Unit, onSend: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth().imePadding(), color = MaterialTheme.colorScheme.background, shadowElevation = 16.dp) {
        Row(modifier = Modifier.padding(16.dp).navigationBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Describe your vision...") },
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
            )
            Spacer(modifier = Modifier.width(12.dp))
            FloatingActionButton(onClick = onSend, containerColor = DeepEmerald, contentColor = Color.White, shape = CircleShape) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

