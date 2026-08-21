package com.denis.realtynova.features.payment

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.denis.realtynova.core.designsystem.components.RealtyNovaButton
import com.denis.realtynova.core.designsystem.components.ButtonVariant
import com.denis.realtynova.core.designsystem.components.RealtyNovaTextField
import com.denis.realtynova.core.designsystem.theme.DeepEmerald
import com.denis.realtynova.core.designsystem.theme.ChampagneGold

import androidx.compose.animation.core.tween
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.denis.realtynova.core.util.PaymentUtils
import com.denis.realtynova.core.designsystem.theme.MidnightEmerald
import com.denis.realtynova.core.designsystem.theme.REALTYNOVATheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    propertyId: String,
    amount: Double,
    onBack: () -> Unit,
    onPaymentSuccess: () -> Unit,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(propertyId) {
        viewModel.loadProperty(propertyId)
    }

    PaymentContent(
        uiState = uiState,
        amount = amount,
        onBack = onBack,
        onPaymentSuccess = onPaymentSuccess,
        onProcessMpesaPayment = viewModel::processMpesaPayment,
        onProcessCardPayment = viewModel::processCardPayment
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentContent(
    uiState: PaymentUiState,
    amount: Double,
    onBack: () -> Unit,
    onPaymentSuccess: () -> Unit,
    onProcessMpesaPayment: (String, Double) -> Unit,
    onProcessCardPayment: (String, String, String, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMethod by remember { mutableStateOf<PaymentMethodType>(PaymentMethodType.MPESA) }
    
    // Form fields
    var phone by remember { mutableStateOf("") }
    var cardNumber by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvc by remember { mutableStateOf("") }
    var saveCard by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.paymentSuccess) {
        if (uiState.paymentSuccess) {
            showSuccessDialog = true
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                onPaymentSuccess()
            },
            confirmButton = {
                Button(
                    onClick = { 
                        showSuccessDialog = false
                        onPaymentSuccess()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DeepEmerald)
                ) {
                    Text("DONE")
                }
            },
            icon = { Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(48.dp)) },
            title = { Text("Payment Successful") },
            text = { Text("Your transaction has been processed successfully. You can now view your reserved property in your profile.") },
            shape = RoundedCornerShape(28.dp)
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Secure Checkout", fontWeight = FontWeight.Bold)
                        Text("VERIFIED ENCRYPTED CONNECTION", style = MaterialTheme.typography.labelSmall, color = Color(0xFF2E7D32), letterSpacing = 1.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Help */ }) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = "Help")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DeepEmerald)
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(vertical = 20.dp)
                ) {
                    item {
                        PropertySummaryCard(uiState.property, amount)
                    }

                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = DeepEmerald, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Select Payment Method",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    item {
                        PaymentMethodsRow(
                            selectedMethod = selectedMethod,
                            onMethodSelected = { selectedMethod = it }
                        )
                    }

                    item {
                        AnimatedContent(
                            targetState = selectedMethod,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) togetherWith
                                fadeOut(animationSpec = tween(300))
                            },
                            label = "PaymentForm"
                        ) { method ->
                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                when (method) {
                                    PaymentMethodType.MPESA, PaymentMethodType.AIRTEL -> {
                                        MobileMoneyForm(
                                            phone = phone,
                                            onPhoneChange = { if (it.length <= 10) phone = it.filter { c -> c.isDigit() } },
                                            label = if (method == PaymentMethodType.MPESA) "M-Pesa Number" else "Airtel Money Number"
                                        )
                                    }
                                    PaymentMethodType.CARD -> {
                                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                            CreditCardVisualizer(
                                                number = cardNumber,
                                                expiry = expiry,
                                                brand = PaymentUtils.getCardBrand(cardNumber)
                                            )
                                            
                                            CardForm(
                                                number = cardNumber,
                                                onNumberChange = { if (it.length <= 16) cardNumber = it.filter { c -> c.isDigit() } },
                                                expiry = expiry,
                                                onExpiryChange = { if (it.length <= 4) expiry = it.filter { c -> c.isDigit() } },
                                                cvc = cvc,
                                                onCvcChange = { if (it.length <= 3) cvc = it.filter { c -> c.isDigit() } }
                                            )
                                            
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.clickable { saveCard = !saveCard }
                                            ) {
                                                Checkbox(
                                                    checked = saveCard,
                                                    onCheckedChange = { saveCard = it },
                                                    colors = CheckboxDefaults.colors(checkedColor = DeepEmerald)
                                                )
                                                Text(
                                                    text = "Securely save card for future payments",
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        }
                                    }
                                    PaymentMethodType.BANK -> {
                                        BankSelection()
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        val isFormValid = when(selectedMethod) {
                            PaymentMethodType.MPESA, PaymentMethodType.AIRTEL -> phone.length >= 9
                            PaymentMethodType.CARD -> cardNumber.length == 16 && expiry.length == 4 && cvc.length == 3
                            PaymentMethodType.BANK -> true
                        }
                        
                        RealtyNovaButton(
                            onClick = {
                                when (selectedMethod) {
                                    PaymentMethodType.MPESA, PaymentMethodType.AIRTEL -> {
                                        onProcessMpesaPayment(phone, amount)
                                    }
                                    PaymentMethodType.CARD -> {
                                        onProcessCardPayment(cardNumber, expiry, cvc, amount)
                                    }
                                    PaymentMethodType.BANK -> { /* Not implemented */ }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            variant = ButtonVariant.Premium,
                            enabled = !uiState.isProcessing && isFormValid
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "PAY KSH ${String.format("%,.0f", amount)}",
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                            }
                        }
                    }
                    
                    item {
                        PaymentFooter()
                    }
                }

                if (uiState.isProcessing) {
                    ProcessingOverlay(selectedMethod)
                }

                if (uiState.paymentSuccess) {
                    SuccessOverlay(onSuccess = onPaymentSuccess)
                }
            }
        }
    }
}

@Composable
fun CreditCardVisualizer(number: String, expiry: String, brand: String) {
    val formattedNumber = number.padEnd(16, '•').chunked(4).joinToString("   ")
    val formattedExpiry = if (expiry.length >= 2) "${expiry.take(2)}/${expiry.drop(2).padEnd(2, '•')}" else expiry.padEnd(2, '•') + "/••"

    Surface(
        modifier = Modifier.fillMaxWidth().height(180.dp),
        shape = RoundedCornerShape(20.dp),
        color = MidnightEmerald,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
            // Card design elements
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-40).dp)
                    .background(Color.White.copy(alpha = 0.05f), CircleShape)
            )

            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Wifi, contentDescription = null, tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(24.dp))
                    Text(
                        text = brand.uppercase(),
                        color = ChampagneGold,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                }

                Text(
                    text = formattedNumber,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    letterSpacing = 2.sp
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("CARD HOLDER", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
                        Text("REALTYNOVA USER", style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("EXPIRES", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.5f))
                        Text(formattedExpiry, style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun BankSelection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Select Bank Transfer", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Direct bank transfers may take up to 24 hours to verify. Your reservation will be marked as 'Pending' until confirmed.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text("CHOOSE BANK")
            }
        }
    }
}

@Composable
fun PaymentFooter() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Https, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "SECURE PCI-DSS COMPLIANT PAYMENT",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF2E7D32),
                letterSpacing = 1.sp
            )
        }
        
        Text(
            text = "By clicking pay, you agree to REALTYNOVA's terms of service and reservation policy. Funds will be held in escrow until lease confirmation.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            fontSize = 10.sp,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun SuccessOverlay(onSuccess: () -> Unit) {
    Dialog(
        onDismissRequest = onSuccess,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = Color(0xFFE8F5E9)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Payment Successful!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Your property reservation has been secured. You can now view your transaction history in your profile.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                RealtyNovaButton(
                    onClick = onSuccess,
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text("CONTINUE TO HOME", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ProcessingOverlay(method: PaymentMethodType) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(64.dp),
                    color = DeepEmerald,
                    strokeWidth = 6.dp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = if (method == PaymentMethodType.MPESA || method == PaymentMethodType.AIRTEL) 
                        "Waiting for STK Push..." 
                    else "Processing Payment...",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (method == PaymentMethodType.MPESA || method == PaymentMethodType.AIRTEL)
                        "Please check your phone and enter your PIN to authorize the transaction."
                    else "Do not close the app while we secure your reservation.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun PropertySummaryCard(property: com.denis.realtynova.core.domain.model.Property?, amount: Double) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = property?.title ?: "Property Reservation",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = property?.location ?: "Kenya",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Total Payable", style = MaterialTheme.typography.bodyLarge)
                Text(
                    text = if (amount.isNaN() || amount.isInfinite()) "KSh 0" else "KSh ${String.format("%,.0f", amount)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = DeepEmerald
                )
            }
        }
    }
}

@Composable
fun PaymentMethodsRow(
    selectedMethod: PaymentMethodType,
    onMethodSelected: (PaymentMethodType) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PaymentMethodItem(
            type = PaymentMethodType.MPESA,
            isSelected = selectedMethod == PaymentMethodType.MPESA,
            onClick = { onMethodSelected(PaymentMethodType.MPESA) },
            modifier = Modifier.weight(1f)
        )
        PaymentMethodItem(
            type = PaymentMethodType.AIRTEL,
            isSelected = selectedMethod == PaymentMethodType.AIRTEL,
            onClick = { onMethodSelected(PaymentMethodType.AIRTEL) },
            modifier = Modifier.weight(1f)
        )
        PaymentMethodItem(
            type = PaymentMethodType.CARD,
            isSelected = selectedMethod == PaymentMethodType.CARD,
            onClick = { onMethodSelected(PaymentMethodType.CARD) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PaymentMethodItem(
    type: PaymentMethodType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) DeepEmerald else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp, 
            if (isSelected) DeepEmerald else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val icon = when(type) {
                PaymentMethodType.MPESA -> Icons.Default.Smartphone
                PaymentMethodType.AIRTEL -> Icons.Default.PhonelinkRing
                PaymentMethodType.CARD -> Icons.Default.CreditCard
                PaymentMethodType.BANK -> Icons.Default.AccountBalance
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = type.displayName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun MobileMoneyForm(phone: String, onPhoneChange: (String) -> Unit, label: String) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        RealtyNovaTextField(
            value = phone,
            onValueChange = onPhoneChange,
            label = label,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
        )
        Text(
            text = "You will receive an M-Pesa prompt on your phone to enter your PIN.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun CardForm(
    number: String, onNumberChange: (String) -> Unit,
    expiry: String, onExpiryChange: (String) -> Unit,
    cvc: String, onCvcChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        RealtyNovaTextField(
            value = number,
            onValueChange = onNumberChange,
            label = "Card Number",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            RealtyNovaTextField(
                value = expiry,
                onValueChange = onExpiryChange,
                label = "MM/YY",
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            RealtyNovaTextField(
                value = cvc,
                onValueChange = onCvcChange,
                label = "CVC",
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}

enum class PaymentMethodType(val displayName: String) {
    MPESA("M-Pesa"),
    AIRTEL("Airtel"),
    CARD("Card"),
    BANK("Bank")
}

@Preview(showBackground = true)
@Composable
fun PaymentScreenPreview() {
    val sampleProperty = com.denis.realtynova.core.domain.model.Property(
        id = "1",
        title = "Luxury Penthouse in Kilimani",
        description = "Beautiful 3-bedroom penthouse with stunning views.",
        price = 150000.0,
        location = "Kilimani, Nairobi",
        address = "123 Argwings Kodhek Rd",
        bedrooms = 3,
        bathrooms = 2.0,
        areaSqFt = 2500.0,
        images = emptyList(),
        type = "Apartment",
        listingType = "Rent"
    )
    
    val uiState = PaymentUiState(
        property = sampleProperty,
        isLoading = false
    )
    
    REALTYNOVATheme {
        PaymentContent(
            uiState = uiState,
            amount = 150000.0,
            onBack = {},
            onPaymentSuccess = {},
            onProcessMpesaPayment = { _, _ -> },
            onProcessCardPayment = { _, _, _, _ -> }
        )
    }
}
