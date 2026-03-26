package com.nisr.sauservices.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.data.model.Order
import com.nisr.sauservices.data.model.InventoryItem
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.viewmodel.ShopkeeperViewModel

// Clean Palette
private val PrimaryBlue = Color(0xFF1E3A8A)
private val Background = Color(0xFFF9FAFB)
private val Surface = Color(0xFFFFFFFF)
private val Border = Color(0xFFF1F5F9)
private val TextDark = Color(0xFF111827)
private val TextGrey = Color(0xFF6B7280)
private val SuccessGreen = Color(0xFF22C55E)
private val PendingOrange = Color(0xFFF97316)
private val ActiveBlue = Color(0xFF3B82F6)

@Composable
fun ShopkeeperDashboardScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: ShopkeeperViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var isChatOpen by remember { mutableStateOf(false) }

    val onLogout = {
        sessionManager.logout()
        navController.navigate(Screen.RoleSelection.route) { popUpTo(0) { inclusive = true } }
    }

    Scaffold(
        containerColor = Background,
        bottomBar = {
            ShopBottomNavigationBar(selectedTab) { selectedTab = it }
        },
        floatingActionButton = {
            ShopFloatingChatButton { isChatOpen = true }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> ShopHomeScreen(viewModel, onLogout)
                1 -> InventoryScreen(viewModel, onLogout)
                2 -> ShopAnalyticsScreen(onLogout)
                3 -> ShopProfileScreen(onLogout)
            }
            
            if (isChatOpen) {
                ShopChatWindow(onClose = { isChatOpen = false })
            }
        }
    }
}

@Composable
fun ShopHomeScreen(viewModel: ShopkeeperViewModel, onLogout: () -> Unit) {
    val orders by viewModel.orders.observeAsState(initial = emptyList())
    var isOnline by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            ShopDashboardHeader(
                title = "Vendor Dashboard",
                onLogout = onLogout
            )
        }

        item {
            Column(modifier = Modifier.padding(16.dp)) {
                // Availability Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Surface,
                    border = BorderStroke(1.dp, Border)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Availability", fontWeight = FontWeight.Bold, color = TextDark)
                            Text(
                                if (isOnline) "Online — Accepting jobs" else "Offline — Not accepting jobs",
                                fontSize = 12.sp,
                                color = if (isOnline) SuccessGreen else TextGrey
                            )
                        }
                        Switch(
                            checked = isOnline,
                            onCheckedChange = { isOnline = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PrimaryBlue
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Stats Grid
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShopStatCard("Orders Today", "12", Icons.Rounded.ShoppingBag, ActiveBlue, Modifier.weight(1f))
                    ShopStatCard("Sales Today", "₹8,400", Icons.Rounded.Payments, SuccessGreen, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ShopStatCard("Low Stock", "5", Icons.Rounded.ErrorOutline, Color.Red, Modifier.weight(1f))
                    ShopStatCard("Earnings", "₹6,200", Icons.Rounded.AccountBalanceWallet, PendingOrange, Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("Active Tracking", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(12.dp))
                ShopMiniMapCard() // Real Map integration

                Spacer(modifier = Modifier.height(24.dp))
                Text("Recent Orders", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        items(orders.take(3)) { order ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                OrderCard(order, viewModel)
            }
        }
    }
}

@Composable
fun InventoryScreen(viewModel: ShopkeeperViewModel, onLogout: () -> Unit) {
    val inventory by viewModel.inventory.collectAsState()
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("Grocery", "Food & Beverages", "Electronics", "Fashion", "Home")

    Column(modifier = Modifier.fillMaxSize()) {
        ShopDashboardHeader(title = "Inventory", onLogout = onLogout, showToggle = false)
        
        Column(modifier = Modifier.padding(16.dp)) {
            // Add New Product Button
            Surface(
                modifier = Modifier.fillMaxWidth().clickable { },
                shape = RoundedCornerShape(12.dp),
                color = Background,
                border = BorderStroke(1.dp, Border.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Rounded.Add, null, tint = TextGrey)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add New Product", color = TextGrey, fontWeight = FontWeight.Medium)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlue.copy(alpha = 0.1f),
                            selectedLabelColor = PrimaryBlue
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(inventory) { item ->
                    InventoryItemCard(item)
                }
            }
        }
    }
}

@Composable
fun ShopAnalyticsScreen(onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        ShopDashboardHeader(title = "Analytics", onLogout = onLogout, showToggle = false)
        Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ShopStatCard("Today's Sales", "₹8,400", Icons.Rounded.AttachMoney, SuccessGreen, Modifier.weight(1f))
                ShopStatCard("Weekly Sales", "₹42,500", Icons.Rounded.TrendingUp, ActiveBlue, Modifier.weight(1f))
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Surface,
                border = BorderStroke(1.dp, Border)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Daily Sales (This Week)", fontSize = 14.sp, color = TextGrey)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        val heights = listOf(0.4f, 0.7f, 0.5f, 0.8f, 0.6f, 0.9f, 0.75f)
                        heights.forEach { h ->
                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .fillMaxHeight(h)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SuccessGreen)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("M", "T", "W", "T", "F", "S", "S").forEach { 
                            Text(it, fontSize = 12.sp, color = TextGrey)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShopProfileScreen(onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        ShopDashboardHeader(title = "Shop Profile", onLogout = onLogout, showToggle = false)
        Column(
            modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Surface,
                border = BorderStroke(1.dp, Border)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(SuccessGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Storefront, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Kumar General Store", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
                        Text("Vendor ID: VN-2045", fontSize = 13.sp, color = TextGrey)
                    }
                }
            }
        }
    }
}

@Composable
fun ShopDashboardHeader(
    title: String,
    onLogout: () -> Unit = {},
    showToggle: Boolean = true
) {
    Surface(
        color = PrimaryBlue,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.statusBarsPadding().padding(horizontal = 16.dp, vertical = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onLogout, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.AutoMirrored.Rounded.Logout, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Rounded.Notifications, null, tint = Color.White)
                }
            }
        }
    }
}

@Composable
fun ShopStatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(label, fontSize = 12.sp, color = TextGrey)
        }
    }
}

@Composable
fun OrderCard(order: Order, viewModel: ShopkeeperViewModel) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(order.customerName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    Text(order.items.joinToString { it.name }, fontSize = 14.sp, color = TextGrey)
                }
                Surface(
                    color = ActiveBlue.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        order.status.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ActiveBlue
                    )
                }
            }
        }
    }
}

@Composable
fun InventoryItemCard(item: InventoryItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Surface,
        border = BorderStroke(1.dp, Border)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)).background(Background),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Image, null, tint = Color.LightGray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, color = TextDark)
                Text("Stock: ${item.stockCount}", fontSize = 12.sp, color = TextGrey)
                Text("₹${item.price.toInt()}", fontWeight = FontWeight.Bold, color = TextDark)
            }
        }
    }
}

@Composable
fun ShopMiniMapCard() {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(28.6139, 77.2090), 14f)
    }

    Surface(
        modifier = Modifier.fillMaxWidth().height(220.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color.LightGray,
        border = BorderStroke(1.dp, Border)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = MapUiSettings(zoomControlsEnabled = false)
        ) {
            Marker(
                state = MarkerState(position = LatLng(28.6139, 77.2090)),
                title = "My Shop",
                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
            )
            // Animated delivery boy marker would be added here via Firebase listener
        }
    }
}

@Composable
fun ShopBottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = Surface,
        contentColor = PrimaryBlue,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Home, null) },
            label = { Text("Home") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.Inventory2, null) },
            label = { Text("Inventory") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.BarChart, null) },
            label = { Text("Analytics") },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Rounded.PersonOutline, null) },
            label = { Text("Profile") },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) }
        )
    }
}

@Composable
fun ShopFloatingChatButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = PendingOrange,
        contentColor = Color.White,
        shape = CircleShape
    ) {
        Icon(Icons.Rounded.Chat, null)
    }
}

@Composable
fun ShopChatWindow(onClose: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)).clickable { onClose() },
        contentAlignment = Alignment.BottomEnd
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(0.85f).fillMaxHeight(0.6f).padding(16.dp).clickable(enabled = false) { },
            shape = RoundedCornerShape(24.dp),
            color = Surface
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().background(PrimaryBlue).padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("SAU Chatbot", color = Color.White, fontWeight = FontWeight.Bold)
                    IconButton(onClose) { Icon(Icons.Rounded.Close, null, tint = Color.White) }
                }
                Box(modifier = Modifier.weight(1f).padding(16.dp)) {
                    Text("How can I help you?", color = TextGrey)
                }
            }
        }
    }
}
