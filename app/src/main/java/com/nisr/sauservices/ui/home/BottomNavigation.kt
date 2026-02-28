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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.viewmodel.CartViewModel

@Composable
fun BottomNavBar(navController: NavController, cartViewModel: CartViewModel = viewModel()) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route
    
    val cartCount = cartViewModel.cartItems.sumOf { it.quantity }

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            NavigationItem("Home", Screen.Home.route, Icons.Outlined.Home),
            NavigationItem("Categories", Screen.Categories.route, Icons.Outlined.GridView),
            NavigationItem("Bookings", Screen.Bookings.route, Icons.Outlined.EventNote),
            NavigationItem("Cart", Screen.Cart.route, Icons.Outlined.ShoppingCart, badgeCount = cartCount),
            NavigationItem("Profile", Screen.Profile.route, Icons.Outlined.Person)
        )

        items.forEach { item ->
            val isSelected = currentRoute == item.route
            
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    if (item.badgeCount > 0) {
                        BadgedBox(badge = { 
                            Badge(containerColor = PinkPrimary) { 
                                Text(item.badgeCount.toString(), color = Color.White) 
                            } 
                        }) {
                            Icon(item.icon, null, modifier = Modifier.size(24.dp))
                        }
                    } else {
                        Icon(item.icon, null, modifier = Modifier.size(24.dp))
                    }
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
