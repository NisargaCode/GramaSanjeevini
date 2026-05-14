package com.example.gramasanjeevini.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gramasanjeevini.GramaSanjeeviniApplication
import com.example.gramasanjeevini.model.Medicine
import com.example.gramasanjeevini.ui.theme.EmergencyRed
import com.example.gramasanjeevini.ui.theme.ExpiryOrange
import com.example.gramasanjeevini.viewmodel.MedicineViewModel
import java.util.*

enum class InventoryFilter { ALL, NEAR_EXPIRY, LOW_STOCK }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmacistDashboard(
    shopId: String, 
    userName: String = "Pharmacist",
    onLogout: () -> Unit = {},
    viewModel: MedicineViewModel = viewModel(
        factory = MedicineViewModel.provideFactory((LocalContext.current.applicationContext as GramaSanjeeviniApplication).repository)
    )
) {
    var showDialog by remember { mutableStateOf(false) }
    var activeFilter by remember { mutableStateOf(InventoryFilter.ALL) }
    
    val medicines by viewModel.getMedicinesByShop(shopId).collectAsState(initial = emptyList())
    
    val nearExpiryList = medicines.filter { it.expiryDate.contains("2023") || it.expiryDate.contains("2024") }
    val lowStockList = medicines.filter { it.quantity < 10 }

    val displayList = when(activeFilter) {
        InventoryFilter.ALL -> medicines
        InventoryFilter.NEAR_EXPIRY -> nearExpiryList
        InventoryFilter.LOW_STOCK -> lowStockList
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Welcome, $userName",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Inventory Management",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { 
                        viewModel.logout()
                        onLogout()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add New Stock") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 16.dp, vertical = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Total", 
                        value = medicines.size.toString(), 
                        isSelected = activeFilter == InventoryFilter.ALL,
                        modifier = Modifier.weight(1f),
                        onClick = { activeFilter = InventoryFilter.ALL }
                    )
                    StatCard(
                        title = "Near Expiry", 
                        value = nearExpiryList.size.toString(), 
                        color = ExpiryOrange,
                        isSelected = activeFilter == InventoryFilter.NEAR_EXPIRY,
                        modifier = Modifier.weight(1f),
                        onClick = { activeFilter = InventoryFilter.NEAR_EXPIRY }
                    )
                    StatCard(
                        title = "Low Stock", 
                        value = lowStockList.size.toString(), 
                        color = EmergencyRed,
                        isSelected = activeFilter == InventoryFilter.LOW_STOCK,
                        modifier = Modifier.weight(1f),
                        onClick = { activeFilter = InventoryFilter.LOW_STOCK }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = when(activeFilter) {
                            InventoryFilter.ALL -> "Full Inventory"
                            InventoryFilter.NEAR_EXPIRY -> "⚠️ Near Expiry Items"
                            InventoryFilter.LOW_STOCK -> "🚨 Low Stock Alert"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                if (displayList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No items to show in this view.", color = Color.Gray)
                        }
                    }
                }

                items(displayList) { med ->
                    InventoryItemCard(
                        med = med, 
                        isExpiryAlert = med.expiryDate.contains("2023") || med.expiryDate.contains("2024")
                    )
                }
            }
        }

        if (showDialog) {
            AddMedicineDialog(
                onDismiss = { showDialog = false },
                onAdd = { newMed ->
                    viewModel.addMedicine(newMed.copy(shopId = shopId))
                    showDialog = false
                }
            )
        }
    }
}

@Composable
fun StatCard(
    title: String, 
    value: String, 
    isSelected: Boolean,
    modifier: Modifier = Modifier, 
    color: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) color else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp), 
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title, 
                style = MaterialTheme.typography.labelSmall,
                color = if (isSelected) Color.White else Color.Gray,
                textAlign = TextAlign.Center
            )
            Text(
                text = value, 
                style = MaterialTheme.typography.headlineSmall, 
                fontWeight = FontWeight.Black, 
                color = if (isSelected) Color.White else color
            )
        }
    }
}

@Composable
fun InventoryItemCard(med: Medicine, isExpiryAlert: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (isExpiryAlert) Color(0xFFFFF3E0) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isExpiryAlert) Icons.Default.Warning else Icons.Default.Info,
                        contentDescription = null,
                        tint = if (med.isEmergency) EmergencyRed else if (isExpiryAlert) ExpiryOrange else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = med.name, 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Expires: ${med.expiryDate}", 
                    style = MaterialTheme.typography.bodySmall, 
                    color = if (isExpiryAlert) ExpiryOrange else Color.Gray,
                    fontWeight = if (isExpiryAlert) FontWeight.Bold else FontWeight.Normal
                )
                if (med.discountPrice != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "Disc: ₹${med.discountPrice}", 
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${med.quantity}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = if (med.quantity < 10) EmergencyRed else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Units", 
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun AddMedicineDialog(onDismiss: () -> Unit, onAdd: (Medicine) -> Unit) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf("2024-12-01") }
    var discountPrice by remember { mutableStateOf("") }
    var isEmergency by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Update Inventory", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Medicine Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("MRP (₹)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = { expiryDate = it },
                    label = { Text("Expiry Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = discountPrice,
                    onValueChange = { discountPrice = it },
                    label = { Text("Discount Price (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFEBEE), RoundedCornerShape(12.dp))
                        .padding(8.dp)
                ) {
                    Checkbox(checked = isEmergency, onCheckedChange = { isEmergency = it })
                    Text(
                        "Emergency / Life Saving Drug", 
                        fontSize = 12.sp, 
                        fontWeight = FontWeight.Bold, 
                        color = EmergencyRed
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAdd(Medicine(
                        UUID.randomUUID().toString(),
                        name,
                        "",
                        quantity.toIntOrNull() ?: 0,
                        expiryDate,
                        isEmergency,
                        price.toDoubleOrNull() ?: 0.0,
                        discountPrice.toDoubleOrNull()
                    ))
                },
                enabled = name.isNotBlank() && quantity.isNotBlank() && price.isNotBlank(),
                shape = RoundedCornerShape(12.dp)
            ) { Text("Confirm Stock") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
