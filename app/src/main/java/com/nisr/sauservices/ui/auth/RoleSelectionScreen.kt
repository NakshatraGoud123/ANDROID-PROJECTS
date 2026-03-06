package com.nisr.sauservices.ui.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.nisr.sauservices.ui.Screen

// Colors updated to match the clean design
private val BrandBlue = Color(0xFF2563EB)
private val TextDark = Color(0xFF1F2937)
private val TextGrey = Color(0xFF6B7280)
private val BorderColor = Color(0xFFE5E7EB)

data class RoleOption(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconColor: Color,
    val iconBg: Color
)

@Composable
fun RoleSelectionScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    val roles = listOf(
        RoleOption(
            id = "customer",
            title = "Customer",
            description = "Book services and shop products.",
            icon = Icons.Rounded.ShoppingBag,
            iconColor = Color(0xFF2563EB),
            iconBg = Color(0xFFEFF6FF)
        ),
        RoleOption(
            id = "service_worker",
            title = "Service Worker",
            description = "Accept service jobs and manage work tasks.",
            icon = Icons.Rounded.Engineering,
            iconColor = Color(0xFF0D9488),
            iconBg = Color(0xFFF0FDFA)
        ),
        RoleOption(
            id = "shopkeeper",
            title = "Shopkeeper / Vendor",
            description = "Sell products, manage inventory, receive orders.",
            icon = Icons.Rounded.Storefront,
            iconColor = Color(0xFFD97706),
            iconBg = Color(0xFFFFFBEB)
        ),
        RoleOption(
            id = "delivery",
            title = "Delivery Partner",
            description = "Deliver customer orders and manage deliveries.",
            icon = Icons.Rounded.LocalShipping,
            iconColor = Color(0xFF16A34A),
            iconBg = Color(0xFFF0FDF4)
        )
    )

    Scaffold(
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Brand Logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "S",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Select Your Role",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose how you want to use SAU",
                fontSize = 16.sp,
                color = TextGrey,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Role Cards
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                roles.forEach { role ->
                    HorizontalRoleCard(
                        role = role,
                        onClick = {
                            navController.navigate(Screen.AuthOptions.createRoute(role.id))
                        }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun HorizontalRoleCard(
    role: RoleOption,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(role.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = role.icon,
                    contentDescription = null,
                    tint = role.iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = role.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = role.description,
                    fontSize = 13.sp,
                    color = TextGrey,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Arrow
            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFD1D5DB),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
