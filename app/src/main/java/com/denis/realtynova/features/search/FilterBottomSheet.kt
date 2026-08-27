package com.denis.realtynova.features.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.denis.realtynova.core.domain.model.SearchFilter
import com.denis.realtynova.core.domain.model.SortOrder
import com.denis.realtynova.core.designsystem.theme.DeepEmerald

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    filter: SearchFilter,
    onFilterChanged: (SearchFilter) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Refine Search",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Property Type
            Text("Property Type", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            FilterChipRow(
                options = listOf("All", "Apartment", "House", "Land", "Commercial"),
                selected = filter.propertyType ?: "All",
                onSelected = { onFilterChanged(filter.copy(propertyType = if (it == "All") null else it)) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Bedrooms
            Text("Min Bedrooms", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            FilterChipRow(
                options = listOf("Any", "1+", "2+", "3+", "4+", "5+"),
                selected = filter.bedrooms?.let { "$it+" } ?: "Any",
                onSelected = { 
                    val value = it.replace("+", "").toIntOrNull()
                    onFilterChanged(filter.copy(bedrooms = value))
                }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Sort Order
            Text("Sort By", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            SortGroup(
                selected = filter.sortBy,
                onSelected = { onFilterChanged(filter.copy(sortBy = it)) }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DeepEmerald)
            ) {
                Text("SHOW RESULTS", fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = { 
                    onFilterChanged(SearchFilter())
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset Filters", color = MaterialTheme.colorScheme.error)
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChipRow(
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelected(option) },
                label = { Text(option) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = DeepEmerald.copy(alpha = 0.1f),
                    selectedLabelColor = DeepEmerald
                )
            )
        }
    }
}

@Composable
fun SortGroup(
    selected: SortOrder,
    onSelected: (SortOrder) -> Unit
) {
    Column {
        SortOrder.entries.forEach { order ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelected(order) }
                    .padding(vertical = 12.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == order,
                    onClick = { onSelected(order) },
                    colors = RadioButtonDefaults.colors(selectedColor = DeepEmerald)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when(order) {
                        SortOrder.NEWEST -> "Newest First"
                        SortOrder.PRICE_LOW_HIGH -> "Price: Low to High"
                        SortOrder.PRICE_HIGH_LOW -> "Price: High to Low"
                        SortOrder.RELEVANCE -> "Most Relevant"
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
