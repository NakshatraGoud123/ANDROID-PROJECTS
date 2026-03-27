package com.nisr.sauservices.ui.essentials

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.nisr.sauservices.data.model.BookingCategory
import com.nisr.sauservices.data.model.BookingItem
import com.nisr.sauservices.data.model.BookingSubcategory
import com.nisr.sauservices.data.model.NewModulesData
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodel.CartViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsModuleScreen(navController: NavController, cartViewModel: CartViewModel) {
    val categories = NewModulesData.bookings
    var selectedCategory by remember { mutableStateOf<BookingCategory?>(null) }
    var selectedSubcategory by remember { mutableStateOf<BookingSubcategory?>(null) }
    var itemToBook by remember { mutableStateOf<BookingItem?>(null) }
    
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Service Bookings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.padding(padding),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                BookingCategoryCard(category) {
                    selectedCategory = category
                }
            }
        }

        if (selectedCategory != null) {
            BookingSubcategoryPopup(
                category = selectedCategory!!,
                onDismiss = { selectedCategory = null },
                onSubcategoryClick = { sub ->
                    selectedSubcategory = sub
                }
            )
        }

        if (selectedSubcategory != null) {
            BookingItemsPopup(
                subcategory = selectedSubcategory!!,
                onDismiss = { selectedSubcategory = null },
                onBookNow = { item ->
                    itemToBook = item
                }
            )
        }

        if (itemToBook != null) {
            SchedulingPopup(
                item = itemToBook!!,
                onDismiss = { itemToBook = null },
                onConfirm = { date, time, qty ->
                    val priceStr = itemToBook!!.priceRange.replace("₹", "").split("–").first().trim()
                    val price = priceStr.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
                    
                    // Add to Cart instead of direct checkout
                    repeat(qty) {
                        cartViewModel.addItemToCart(
                            name = itemToBook!!.name,
                            price = price,
                            category = selectedCategory?.name ?: "Booking",
                            subcategory = selectedSubcategory?.name ?: "",
                            unit = "Booking",
                            productId = itemToBook!!.id
                        )
                    }
                    
                    Toast.makeText(context, "${itemToBook!!.name} added to cart", Toast.LENGTH_SHORT).show()
                    navController.navigate(Screen.Cart.route)
                    
                    itemToBook = null
                    selectedSubcategory = null
                    selectedCategory = null
                }
            )
        }
    }
}

@Composable
fun SchedulingPopup(
    item: BookingItem,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Int) -> Unit
) {
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var quantity by remember { mutableIntStateOf(1) }
    
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            selectedTime = String.format("%02d:%02d", hourOfDay, minute)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = "Schedule Booking", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = item.name, color = Color.Gray, modifier = Modifier.padding(bottom = 16.dp))

                OutlinedCard(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, null)
                        Spacer(Modifier.width(12.dp))
                        Text(if (selectedDate.isEmpty()) "Select Date" else selectedDate)
                    }
                }

                OutlinedCard(
                    onClick = { timePickerDialog.show() },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, null)
                        Spacer(Modifier.width(12.dp))
                        Text(if (selectedTime.isEmpty()) "Select Time" else selectedTime)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Quantity", fontWeight = FontWeight.SemiBold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }) {
                            Text("-", fontSize = 24.sp)
                        }
                        Text(quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp), fontWeight = FontWeight.Bold)
                        IconButton(onClick = { quantity++ }) {
                            Text("+", fontSize = 24.sp)
                        }
                    }
                }

                Button(
                    onClick = {
                        if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                            onConfirm(selectedDate, selectedTime, quantity)
                        } else {
                            Toast.makeText(context, "Please select date and time", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("ADD TO CART")
                }
            }
        }
    }
}

@Composable
fun BookingCategoryCard(category: BookingCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF))
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = category.name,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun BookingSubcategoryPopup(
    category: BookingCategory,
    onDismiss: () -> Unit,
    onSubcategoryClick: (BookingSubcategory) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.6f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = category.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(category.subcategories) { sub ->
                        ListItem(
                            headlineContent = { Text(sub.name) },
                            modifier = Modifier.clickable { onSubcategoryClick(sub) }
                        )
                        HorizontalDivider()
                    }
                }
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun BookingItemsPopup(
    subcategory: BookingSubcategory,
    onDismiss: () -> Unit,
    onBookNow: (BookingItem) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.7f)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = subcategory.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(subcategory.items) { item ->
                        BookingItemRow(item) {
                            onBookNow(item)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Back")
                }
            }
        }
    }
}

@Composable
fun BookingItemRow(item: BookingItem, onBook: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, fontWeight = FontWeight.SemiBold)
            Text(text = item.priceRange, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
        }
        Button(onClick = onBook, shape = RoundedCornerShape(8.dp)) {
            Text("BOOK NOW")
        }
    }
}
