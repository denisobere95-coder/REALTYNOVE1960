package com.denis.realtynova.features.messages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.denis.realtynova.core.designsystem.components.CreativeBackground
import com.denis.realtynova.core.designsystem.components.BackgroundVariant
import com.denis.realtynova.R
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.Graphite
import com.denis.realtynova.core.designsystem.theme.SlateGray
import androidx.compose.ui.tooling.preview.Preview
import com.denis.realtynova.core.designsystem.theme.REALTYNOVATheme
import com.denis.realtynova.core.domain.model.RecentChat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessagesScreen(
    viewModel: MessagesViewModel = hiltViewModel(),
    onChatClick: (String) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    MessagesScreenContent(
        uiState = uiState,
        onChatClick = onChatClick,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreenContent(
    uiState: MessagesUiState,
    onChatClick: (String) -> Unit,
    onBack: () -> Unit
) {
    CreativeBackground(
        imageRes = R.drawable.img_4,
        variant = BackgroundVariant.EMERALD,
        overlayAlpha = 0.85f
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Messages", fontWeight = FontWeight.Bold, color = Color.White) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(imageVector = Icons.Default.EditNote, contentDescription = "New Message", tint = Color.White)
                        }
                    }
                )
            }
        ) { innerPadding ->
            when (val state = uiState) {
                is MessagesUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ChampagneGold)
                    }
                }
                is MessagesUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is MessagesUiState.Success -> {
                    if (state.chats.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = "No messages yet", color = Color.White.copy(alpha = 0.6f))
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.chats) { chat ->
                                ChatListItem(chat = chat, onClick = { onChatClick(chat.otherUserId) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatListItem(chat: RecentChat, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                if (chat.otherUserPhoto != null) {
                    AsyncImage(
                        model = chat.otherUserPhoto,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = chat.otherUserName.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = DeepEmerald
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = chat.otherUserName,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Graphite
                    )
                    Text(
                        text = formatTimestamp(chat.lastMessageTime),
                        fontSize = 12.sp,
                        color = SlateGray,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = chat.lastMessage,
                    fontSize = 14.sp,
                    color = if (chat.unreadCount > 0) Graphite else SlateGray,
                    fontWeight = if (chat.unreadCount > 0) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            if (chat.unreadCount > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Badge(containerColor = DeepEmerald) {
                    Text(text = chat.unreadCount.toString(), color = Color.White)
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val now = Calendar.getInstance()
    val chatDate = Calendar.getInstance().apply { time = date }
    
    return if (now.get(Calendar.DATE) == chatDate.get(Calendar.DATE)) {
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
    } else {
        SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
    }
}

@Preview(showBackground = true)
@Composable
fun MessagesScreenPreview() {
    REALTYNOVATheme {
        MessagesScreenContent(
            uiState = MessagesUiState.Success(
                chats = listOf(
                    RecentChat(
                        otherUserId = "1",
                        otherUserName = "John Doe",
                        otherUserPhoto = null,
                        lastMessage = "Is the luxury villa still available for next month?",
                        lastMessageTime = System.currentTimeMillis(),
                        unreadCount = 2
                    ),
                    RecentChat(
                        otherUserId = "2",
                        otherUserName = "Sarah Smith",
                        otherUserPhoto = null,
                        lastMessage = "Thank you for the quick response!",
                        lastMessageTime = System.currentTimeMillis() - 3600000,
                        unreadCount = 0
                    ),
                    RecentChat(
                        otherUserId = "3",
                        otherUserName = "Michael Brown",
                        otherUserPhoto = null,
                        lastMessage = "I'd like to schedule a viewing.",
                        lastMessageTime = System.currentTimeMillis() - 86400000,
                        unreadCount = 1
                    )
                )
            ),
            onChatClick = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MessagesScreenLoadingPreview() {
    REALTYNOVATheme {
        MessagesScreenContent(
            uiState = MessagesUiState.Loading,
            onChatClick = {},
            onBack = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MessagesScreenEmptyPreview() {
    REALTYNOVATheme {
        MessagesScreenContent(
            uiState = MessagesUiState.Success(emptyList()),
            onChatClick = {},
            onBack = {}
        )
    }
}
