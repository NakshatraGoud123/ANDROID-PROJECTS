package com.nisr.sauservices.ui.essentials

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
                    CategoryCardSmall(category) {
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
            SubcategoryPopupSmall(
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
fun CategoryCardSmall(category: SupplyCategory, onClick: () -> Unit) {
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
fun SubcategoryPopupSmall(
    category: SupplyCategory,
    onDismiss: () -> Unit,
    onAddToCart: (SupplySubcategory) -> Unit
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(vertical = 24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = category.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                
                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(category.subcategories) { sub ->
                        SubcategoryItemSmall(sub) {
                            onAddToCart(sub)
                            Toast.makeText(context, "${sub.name} added to cart", Toast.LENGTH_SHORT).show()
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))
                    }
                }
                
                Spacer(Modifier.height(12.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SubcategoryItemSmall(sub: SupplySubcategory, onAdd: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(text = sub.name, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color.Black)
            Text(text = sub.priceRange, color = Color.Gray, fontSize = 13.sp)
        }
        Button(
            onClick = onAdd,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Text("Add to Cart", fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
