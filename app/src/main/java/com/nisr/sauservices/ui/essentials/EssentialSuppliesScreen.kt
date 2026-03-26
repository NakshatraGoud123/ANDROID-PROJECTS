package com.nisr.sauservices.ui.essentials

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
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.nisr.sauservices.data.model.NewModulesData
import com.nisr.sauservices.data.model.SupplyCategory
import com.nisr.sauservices.data.model.SupplySubcategory
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EssentialSuppliesScreen(navController: NavController, viewModel: CartViewModel) {
    val categories = NewModulesData.essentialSupplies
    var selectedCategory by remember { mutableStateOf<SupplyCategory?>(null) }
    val cartItems by viewModel.dbCartItems.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Essential Supplies", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    CategoryCard(category) {
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
            SubcategoryPopup(
                category = selectedCategory!!,
                onDismiss = { selectedCategory = null },
                onAddToCart = { sub ->
                    val priceStr = sub.priceRange.replace("₹", "").split("–").first().trim()
                    val price = priceStr.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
                    viewModel.addItemToCart(
                        name = sub.name,
                        price = price,
                        category = selectedCategory?.name ?: "",
                        subcategory = sub.name,
                        unit = sub.itemType,
                        productId = sub.id
                    )
                }
            )
        }
    }
}

@Composable
fun CategoryCard(category: SupplyCategory, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
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
fun SubcategoryPopup(
    category: SupplyCategory,
    onDismiss: () -> Unit,
    onAddToCart: (SupplySubcategory) -> Unit
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
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
                        SubcategoryItem(sub) {
                            onAddToCart(sub)
                            Toast.makeText(context, "${sub.name} added to cart", Toast.LENGTH_SHORT).show()
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun SubcategoryItem(sub: SupplySubcategory, onAdd: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = sub.name, fontWeight = FontWeight.SemiBold)
            Text(text = sub.priceRange, color = Color.Gray, fontSize = 14.sp)
        }
        Button(onClick = onAdd, shape = RoundedCornerShape(8.dp)) {
            Text("Add to Cart")
        }
    }
}
