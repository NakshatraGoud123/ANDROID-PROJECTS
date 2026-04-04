package com.nisr.sauservices.ui.home

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary

@Composable
fun BottomNavBar(
    navController: NavController
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            NavigationItem("Home", Screen.Home.route, Icons.Outlined.Home),
            NavigationItem("Categories", Screen.Categories.route, Icons.Outlined.Category),
            NavigationItem("Orders", Screen.MyOrders.route, Icons.Outlined.ReceiptLong),
            NavigationItem("Track", Screen.OrderTracking.route, Icons.Outlined.MyLocation),
            NavigationItem("Profile", Screen.Profile.route, Icons.Outlined.Person)
        )

        items.forEach { item ->
            val isSelected = currentRoute == item.route || (item.route == Screen.OrderTracking.route && currentRoute?.startsWith("order_tracking") == true)
            
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (item.route == Screen.OrderTracking.route) {
                        navController.navigate(Screen.OrderTracking.createRoute("active_order"))
                    } else if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(item.icon, null, modifier = Modifier.size(24.dp))
                },
                label = { Text(item.title, fontSize = 10.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PinkPrimary,
                    selectedTextColor = PinkPrimary,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class NavigationItem(
    val title: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val badgeCount: Int = 0
)
