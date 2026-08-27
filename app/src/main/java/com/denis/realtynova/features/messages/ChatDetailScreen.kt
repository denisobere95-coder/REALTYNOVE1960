package com.denis.realtynova.features.messages

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.tooling.preview.Preview
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.Graphite
import com.denis.realtynova.core.designsystem.theme.REALTYNOVATheme
import com.denis.realtynova.core.designsystem.components.CreativeBackground
import com.denis.realtynova.core.designsystem.components.BackgroundVariant
import com.denis.realtynova.R
import com.denis.realtynova.core.domain.model.Message
import com.denis.realtynova.core.domain.model.User
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    viewModel: ChatDetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    ChatDetailContent(
        uiState = uiState,
        onBack = onBack,
        onSendMessage = { viewModel.sendMessage(it) },
        onTyping = viewModel::setTyping
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChatDetailContent(
    uiState: ChatUiState,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onTyping: (Boolean) -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Typing debounce
    LaunchedEffect(messageText) {
        if (messageText.isNotEmpty()) {
            onTyping(true)
            delay(2.seconds)
            onTyping(false)
        } else {
            onTyping(false)
        }
    }

    CreativeBackground(
        imageRes = R.drawable.img_18,
        variant = BackgroundVariant.EMERALD,
        overlayAlpha = 0.9f
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Chat", fontWeight = FontWeight.Bold, color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    }
                )
            },
            bottomBar = {
                Surface(tonalElevation = 2.dp, color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .navigationBarsPadding()
                            .imePadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Type a message...") },
                            shape = RoundedCornerShape(24.dp),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    onSendMessage(messageText)
                                    messageText = ""
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = DeepEmerald,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                        }
                    }
                }
            }
        ) { innerPadding ->
            when (uiState) {
                is ChatUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ChampagneGold)
                    }
                }
                is ChatUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is ChatUiState.Success -> {
                    LaunchedEffect(uiState.messages.size) {
                        if (uiState.messages.isNotEmpty()) {
                            listState.animateScrollToItem(uiState.messages.size - 1)
                        }
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.messages) { message ->
                            val isCurrentUser = message.senderId == uiState.currentUser?.id
                            MessageBubble(message = message, isCurrentUser = isCurrentUser)
                        }

                        if (uiState.isOtherUserTyping) {
                            item {
                                TypingIndicator(modifier = Modifier.padding(start = 8.dp, bottom = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TypingIndicator(modifier: Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(3) { index ->
            val dotAlpha by rememberInfiniteTransition().animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "TypingDot"
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color.Gray.copy(alpha = dotAlpha), CircleShape)
            )
        }
        Text("typing...", fontSize = 12.sp, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
    }
}

@Composable
fun MessageBubble(message: Message, isCurrentUser: Boolean) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isCurrentUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isCurrentUser) DeepEmerald else Color.White,
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isCurrentUser) 18.dp else 2.dp,
                bottomEnd = if (isCurrentUser) 2.dp else 18.dp
            ),
            shadowElevation = 2.dp,
            border = if (!isCurrentUser) androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)) else null
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                color = if (isCurrentUser) Color.White else Graphite,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )
        }
        Text(
            text = formatTime(message.createdAt),
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
        )
    }
}

private fun formatTime(timestamp: Long): String {
    return SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
}

@Preview(showBackground = true)
@Composable
fun ChatDetailScreenPreview() {
    val previewUser = User(
        id = "user1",
        email = "denis@example.com",
        phoneNumber = "+123456789",
        displayName = "Denis",
        photoUrl = null
    )

    val previewMessages = listOf(
        Message(
            id = "1",
            senderId = "user2",
            receiverId = "user1",
            content = "Hello! Is the property still available?",
            createdAt = System.currentTimeMillis() - 3600000
        ),
        Message(
            id = "2",
            senderId = "user1",
            receiverId = "user2",
            content = "Yes, it is. Would you like to schedule a viewing?",
            createdAt = System.currentTimeMillis() - 3000000
        ),
        Message(
            id = "3",
            senderId = "user2",
            receiverId = "user1",
            content = "That would be great. How about tomorrow at 2 PM?",
            createdAt = System.currentTimeMillis() - 600000
        )
    )

    REALTYNOVATheme {
        ChatDetailContent(
            uiState = ChatUiState.Success(
                messages = previewMessages,
                currentUser = previewUser,
                otherUserId = "user2",
                isOtherUserTyping = true
            ),
            onBack = {},
            onSendMessage = {},
            onTyping = {}
        )
    }
}
