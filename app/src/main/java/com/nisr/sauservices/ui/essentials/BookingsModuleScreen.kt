package com.nisr.sauservices.ui.essentials

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.nisr.sauservices.data.model.BookingCategory
import com.nisr.sauservices.data.model.BookingItem
import com.nisr.sauservices.data.model.BookingSubcategory
import com.nisr.sauservices.data.model.NewModulesData
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary
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
    val cartItems by cartViewModel.dbCartItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Service Bookings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Cart.route) }) {
                        BadgedBox(badge = {
                            if (cartItems.isNotEmpty()) {
                                Badge(containerColor = PinkPrimary) {
                                    val count = cartItems.sumOf { it.quantity }
                                    Text(count.toString(), color = Color.White)
                                }
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8F8F8))) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { category ->
                    BookingCategoryCardSmall(category) {
                        selectedCategory = category
                    }
                }
            }

            if (cartItems.isNotEmpty()) {
                Button(
                    onClick = { navController.navigate(Screen.Cart.route) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                ) {
                    Text("Go to Cart & Checkout", fontWeight = FontWeight.Bold)
                }
            }
        }

        if (selectedCategory != null) {
            BookingSubcategoryPopupSmall(
                category = selectedCategory!!,
                onDismiss = { selectedCategory = null },
                onSubcategoryClick = { sub ->
                    selectedSubcategory = sub
                }
            )
        }

        if (selectedSubcategory != null) {
            BookingItemsPopupSmall(
                subcategory = selectedSubcategory!!,
                onDismiss = { selectedSubcategory = null },
                onBookNow = { item ->
                    itemToBook = item
                }
            )
        }

        if (itemToBook != null) {
            SchedulingPopupSmall(
                item = itemToBook!!,
                onDismiss = { itemToBook = null },
                onConfirm = { date, time, qty ->
                    val priceStr = itemToBook!!.priceRange.replace("₹", "").split("–").first().trim()
                    val price = priceStr.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
                    
                    cartViewModel.addItemToCart(
                        name = itemToBook!!.name,
                        price = price,
                        category = selectedCategory?.name ?: "Booking",
                        subcategory = selectedSubcategory?.name ?: "",
                        unit = "Booking",
                        productId = itemToBook!!.id,
                        date = date,
                        time = time,
                        quantity = qty
                    )
                    
                    Toast.makeText(context, "${itemToBook!!.name} added to cart", Toast.LENGTH_SHORT).show()
                    
                    itemToBook = null
                    selectedSubcategory = null
                    selectedCategory = null
                    
                    // Navigate to Cart immediately as per user request to go to checkout pipeline
                    navController.navigate(Screen.Cart.route)
                }
            )
        }
    }
}

@Composable
fun BookingCategoryCardSmall(category: BookingCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(12.dp)) {
            Text(
                text = category.name,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun BookingSubcategoryPopupSmall(
    category: BookingCategory,
    onDismiss: () -> Unit,
    onSubcategoryClick: (BookingSubcategory) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(vertical = 24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = category.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(category.subcategories) { sub ->
                        ListItem(
                            headlineContent = { Text(sub.name, fontWeight = FontWeight.Medium) },
                            modifier = Modifier.clickable { onSubcategoryClick(sub) },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                    }
                }
                Spacer(Modifier.height(16.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Close", color = PinkPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BookingItemsPopupSmall(
    subcategory: BookingSubcategory,
    onDismiss: () -> Unit,
    onBookNow: (BookingItem) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(vertical = 24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = subcategory.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(subcategory.items) { item ->
                        BookingItemRowSmall(item) {
                            onBookNow(item)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
                    }
                }
                Spacer(Modifier.height(16.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Back", color = PinkPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun BookingItemRowSmall(item: BookingItem, onBook: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(text = item.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(text = item.priceRange, color = PinkPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Button(
            onClick = onBook, 
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Text("BOOK NOW", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SchedulingPopupSmall(
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
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(text = "Schedule Booking", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = item.name, color = Color.Gray, modifier = Modifier.padding(bottom = 20.dp), fontSize = 14.sp)

                OutlinedCard(
                    onClick = { datePickerDialog.show() },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarMonth, null, tint = PinkPrimary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(if (selectedDate.isEmpty()) "Select Date" else selectedDate, fontSize = 14.sp)
                    }
                }

                OutlinedCard(
                    onClick = { timePickerDialog.show() },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, null, tint = PinkPrimary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(if (selectedTime.isEmpty()) "Select Time" else selectedTime, fontSize = 14.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Quantity", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }, modifier = Modifier.size(32.dp)) {
                            Text("-", fontSize = 20.sp, color = PinkPrimary, fontWeight = FontWeight.Bold)
                        }
                        Text(quantity.toString(), modifier = Modifier.padding(horizontal = 12.dp), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        IconButton(onClick = { quantity++ }, modifier = Modifier.size(32.dp)) {
                            Text("+", fontSize = 20.sp, color = PinkPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                            onConfirm(selectedDate, selectedTime, quantity)
                        } else {
                            Toast.makeText(context, "Please select date and time", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                ) {
                    Text("PROCEED TO CHECKOUT", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
