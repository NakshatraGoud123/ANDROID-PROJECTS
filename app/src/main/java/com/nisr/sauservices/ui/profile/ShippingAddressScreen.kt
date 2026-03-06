package com.nisr.sauservices.ui.profile

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.nisr.sauservices.ui.viewmodel.Address
import com.nisr.sauservices.ui.viewmodel.ProfileViewModel

// Local theme color if PinkPrimary is missing
private val PinkPrimary = Color(0xFFE91E63)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingAddressScreen(navController: NavController, viewModel: ProfileViewModel = viewModel()) {
    val addresses by viewModel.addresses.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shipping Address", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = PinkPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Address")
            }
        },
        containerColor = Color.White
    ) { padding ->
        if (addresses.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No addresses saved yet", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(addresses) { address ->
                    AddressItem(
                        address = address,
                        onDelete = { viewModel.deleteAddress(address.id) },
                        onSetDefault = { viewModel.setDefaultAddress(address.id) }
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        AddAddressDialog(
            onDismiss = { showAddDialog = false },
            onSave = { 
                viewModel.addAddress(it)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddressItem(address: Address, onDelete: () -> Unit, onSetDefault: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9F9F9),
        border = BorderStroke(1.dp, if (address.isDefault) PinkPrimary else Color(0xFFEEEEEE))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(address.fullName, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                if (address.isDefault) {
                    Surface(
                        color = PinkPrimary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "Default",
                            color = PinkPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text("${address.houseNo}, ${address.street}", fontSize = 14.sp, color = Color.Gray)
            Text("${address.city}, ${address.state} - ${address.pincode}", fontSize = 14.sp, color = Color.Gray)
            Text("Phone: ${address.phone}", fontSize = 14.sp, color = Color.Gray)
            
            Spacer(Modifier.height(12.dp))
            
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                if (!address.isDefault) {
                    TextButton(onClick = onSetDefault) {
                        Text("Set as Default", color = PinkPrimary, fontSize = 12.sp)
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressDialog(onDismiss: () -> Unit, onSave: (Address) -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var houseNo by remember { mutableStateOf("") }
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var landmark by remember { mutableStateOf("") }
    var isDefault by remember { mutableStateOf(false) }

    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Address", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = fullName, 
                    onValueChange = { fullName = it }, 
                    label = { Text("Full Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone, 
                    onValueChange = { phone = it }, 
                    label = { Text("Phone") }, 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = houseNo, 
                    onValueChange = { houseNo = it }, 
                    label = { Text("House No") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = street, 
                    onValueChange = { street = it }, 
                    label = { Text("Street") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = city, 
                    onValueChange = { city = it }, 
                    label = { Text("City") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state, 
                    onValueChange = { state = it }, 
                    label = { Text("State") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = pincode, 
                    onValueChange = { pincode = it }, 
                    label = { Text("Pincode") }, 
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = landmark, 
                    onValueChange = { landmark = it }, 
                    label = { Text("Landmark") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isDefault, onCheckedChange = { isDefault = it })
                    Text("Set as Default")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (fullName.isNotEmpty() && phone.length == 10 && pincode.length == 6) {
                        onSave(Address("", fullName, phone, houseNo, street, city, state, pincode, landmark, isDefault))
                    } else {
                        Toast.makeText(context, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
