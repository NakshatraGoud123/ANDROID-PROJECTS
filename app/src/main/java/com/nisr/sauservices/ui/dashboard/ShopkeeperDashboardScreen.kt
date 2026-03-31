package com.nisr.sauservices.ui.dashboard

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.data.model.OrderModel
import com.nisr.sauservices.ui.viewmodels.ShopkeeperViewModel

private val PrimaryBlue = Color(0xFF1E3A8A)
private val Background = Color(0xFFF9FAFB)
private val Surface = Color(0xFFFFFFFF)
private val Border = Color(0xFFF1F5F9)
private val TextDark = Color(0xFF111827)
private val TextGrey = Color(0xFF6B7280)
private val SuccessGreen = Color(0xFF22C55E)
private val PendingOrange = Color(0xFFF97316)
private val InfoBlue = Color(0xFF3B82F6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopkeeperDashboardScreen(
    navController: NavController,
    sessionManager: SessionManager,
    viewModel: ShopkeeperViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAssignSheet by remember { mutableStateOf(false) }
    var selectedOrderForAssign by remember { mutableStateOf<OrderModel?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val deliveryBoys by viewModel.deliveryBoys.collectAsState()
    val context = LocalContext.current

    val onLogout = {
        sessionManager.logout()
        navController.navigate("role_selection") { popUpTo(0) { inclusive = true } }
    }

    if (showAssignSheet && selectedOrderForAssign != null) {
        ModalBottomSheet(
            onDismissRequest = { showAssignSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth().padding(bottom = 32.dp)) {
                Text("Assign Delivery Partner", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn {
                    items(deliveryBoys) { boy ->
                        ListItem(
                            headlineContent = { Text(boy.name) },
                            supportingContent = { Text(boy.phone) },
                            leadingContent = { Icon(Icons.Rounded.Person, null, tint = InfoBlue) },
                            modifier = Modifier.clickable {
                                viewModel.assignDeliveryBoy(selectedOrderForAssign!!.orderId, boy.userId)
                                showAssignSheet = false
                                Toast.makeText(context, "Assigned to ${boy.name}", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Shop Dashboard", color = Color.White) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Rounded.Logout, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryBlue)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Surface) {
                val tabs = listOf(
                    Triple("New", Icons.Rounded.NewReleases, 0),
                    Triple("Accepted", Icons.Rounded.CheckCircle, 1),
                    Triple("Assigned", Icons.Rounded.LocalShipping, 2),
                    Triple("Done", Icons.Rounded.History, 3)
                )
                tabs.forEach { (label, icon, index) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(icon, null) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { padding ->
        val pending by viewModel.pendingOrders.collectAsState()
        val accepted by viewModel.acceptedOrders.collectAsState()
        val assigned by viewModel.assignedOrders.collectAsState()
        val completed by viewModel.completedOrders.collectAsState()

        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> OrderList(pending, "No new orders") { viewModel.acceptOrder(it.orderId) }
                1 -> OrderList(accepted, "No accepted orders", "Assign Delivery") { 
                    selectedOrderForAssign = it
                    showAssignSheet = true
                }
                2 -> OrderList(assigned, "No active deliveries", isAssigned = true)
                3 -> OrderList(completed, "No completed orders")
            }
        }
    }
}

@Composable
fun OrderList(
    orders: List<OrderModel>, 
    emptyMsg: String, 
    btnText: String? = "Accept Order",
    isAssigned: Boolean = false,
    onAction: (OrderModel) -> Unit = {}
) {
    if (orders.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(emptyMsg, color = TextGrey)
        }
    } else {
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(orders) { order ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    border = BorderStroke(1.dp, Border)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Order #${order.orderId.takeLast(6)}", fontWeight = FontWeight.Bold)
                            Text("₹${order.totalPrice}", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                        }
                        Text(order.address, fontSize = 13.sp, color = TextGrey, maxLines = 2)
                        if (btnText != null && !isAssigned && order.orderStatus != "delivered") {
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { onAction(order) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(btnText)
                            }
                        }
                        if (isAssigned) {
                            Text("Partner: ${order.assignedDeliveryBoy.takeLast(6)}", fontSize = 12.sp, color = SuccessGreen)
                        }
                    }
                }
            }
        }
    }
}
