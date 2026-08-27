package com.denis.realtynova.features.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.KeyboardVoice
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.denis.realtynova.core.designsystem.components.CreativeBackground
import com.denis.realtynova.core.designsystem.components.BackgroundVariant
import com.denis.realtynova.R
import com.denis.realtynova.core.designsystem.components.RealtyNovaButton
import com.denis.realtynova.core.designsystem.components.RealtyNovaTextField
import com.denis.realtynova.core.designsystem.theme.ChampagneGold
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.REALTYNOVATheme
import com.denis.realtynova.core.domain.model.User
import com.denis.realtynova.features.auth.AuthViewModel
import com.denis.realtynova.core.util.BiometricManager
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val user by viewModel.currentUser.collectAsState()
    val isBiometricEnabled by viewModel.isBiometricEnabled.collectAsState()
    val isScreenshotPreventionEnabled by viewModel.isScreenshotPreventionEnabled.collectAsState()
    val context = LocalContext.current
    val biometricManager = remember { BiometricManager() }
    val isBiometricAvailable = remember(context) { 
        try {
            biometricManager.isBiometricAvailable(context)
        } catch (e: Exception) {
            false
        }
    }

    CreativeBackground(
        imageRes = R.drawable.img_15,
        variant = BackgroundVariant.EMERALD,
        overlayAlpha = 0.85f
    ) {
        EditProfileContent(
            user = user,
            isBiometricEnabled = isBiometricEnabled,
            isScreenshotPreventionEnabled = isScreenshotPreventionEnabled,
            isBiometricAvailable = isBiometricAvailable,
            onBack = onBack,
            onBiometricToggle = { viewModel.setBiometricEnabled(it) },
            onScreenshotPreventionToggle = { viewModel.setScreenshotPreventionEnabled(it) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileContent(
    user: User?,
    isBiometricEnabled: Boolean,
    isScreenshotPreventionEnabled: Boolean,
    isBiometricAvailable: Boolean,
    onBack: () -> Unit,
    onBiometricToggle: (Boolean) -> Unit,
    onScreenshotPreventionToggle: (Boolean) -> Unit
) {
    var name by remember(user) { mutableStateOf(user?.displayName ?: "") }
    var email by remember(user) { mutableStateOf(user?.email ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> selectedImageUri = uri }
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Profile Picture
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                        .clickable { photoPickerLauncher.launch("image/*") },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    val model = selectedImageUri ?: user?.photoUrl
                    if (model != null) {
                        AsyncImage(
                            model = model,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = name.take(1).uppercase(), fontSize = 48.sp, fontWeight = FontWeight.Bold, color = DeepEmerald)
                        }
                    }
                }
                
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = CircleShape,
                    color = ChampagneGold,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            RealtyNovaTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                placeholder = "Enter your name"
            )

            RealtyNovaTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                placeholder = "name@example.com",
                enabled = false // Usually email is not editable directly in this flow
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "SECURITY & PRIVACY", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = DeepEmerald)

            if (isBiometricAvailable) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.padding(16.dp).clickable { onBiometricToggle(!isBiometricEnabled) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, tint = DeepEmerald)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Biometric Sign-in", fontWeight = FontWeight.Bold)
                                Text(text = "Use fingerprint or face to unlock app", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isBiometricEnabled,
                                onCheckedChange = { onBiometricToggle(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = DeepEmerald)
                            )
                        }
                        
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        Row(
                            modifier = Modifier.padding(16.dp).clickable { /* Mock Voice logic */ },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.KeyboardVoice, contentDescription = null, tint = DeepEmerald)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Voice Authentication", fontWeight = FontWeight.Bold)
                                Text(text = "AI-powered voice pattern verification", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = false, // Mock
                                onCheckedChange = { },
                                colors = SwitchDefaults.colors(checkedThumbColor = DeepEmerald)
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                        Row(
                            modifier = Modifier.padding(16.dp).clickable { onScreenshotPreventionToggle(!isScreenshotPreventionEnabled) },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.PrivacyTip, contentDescription = null, tint = DeepEmerald)
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "Privacy Shield", fontWeight = FontWeight.Bold)
                                Text(text = "Prevent screenshots and screen recording", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = isScreenshotPreventionEnabled,
                                onCheckedChange = { onScreenshotPreventionToggle(it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = DeepEmerald)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            RealtyNovaButton(
                onClick = { 
                    // Update profile logic would go here
                    onBack() 
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("SAVE CHANGES", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfileScreenPreview() {
    val previewUser = User(
        id = "user1",
        email = "denis@example.com",
        phoneNumber = "+123456789",
        displayName = "Denis",
        photoUrl = null
    )
    REALTYNOVATheme {
        EditProfileContent(
            user = previewUser,
            isBiometricEnabled = true,
            isScreenshotPreventionEnabled = false,
            isBiometricAvailable = true,
            onBack = {},
            onBiometricToggle = {},
            onScreenshotPreventionToggle = {}
        )
    }
}
