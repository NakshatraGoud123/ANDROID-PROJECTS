package com.nisr.sauservices.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen

// Colors as per design rules
private val ElegantTeal = Color(0xFF0FA3A3)
private val ElegantTealDark = Color(0xFF087E7E)
private val SoftGreyBgEnd = Color(0xFFECEFF1)
private val TextGrey = Color(0xFF717171)
private val TextDark = Color(0xFF1A1C1E)

data class RoleOption(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val tintColor: Color,
    val bgColor: Color
)

@Composable
fun RoleSelectionScreen(navController: NavController) {
    var selectedRoleId by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    val roles = listOf(
        RoleOption(
            id = "customer",
            title = "Customer",
            description = "Browse & order services",
            icon = Icons.Rounded.ShoppingBag,
            tintColor = Color(0xFF00796B),
            bgColor = Color(0xFFE0F2F1)
        ),
        RoleOption(
            id = "shopkeeper",
            title = "Shopkeeper",
            description = "Manage your shop",
            icon = Icons.Rounded.Storefront,
            tintColor = Color(0xFFE65100),
            bgColor = Color(0xFFFFF3E0)
        ),
        RoleOption(
            id = "service_worker",
            title = "Worker",
            description = "Find work & earn",
            icon = Icons.Rounded.Engineering,
            tintColor = Color(0xFF1565C0),
            bgColor = Color(0xFFE3F2FD)
        ),
        RoleOption(
            id = "delivery",
            title = "Delivery",
            description = "Deliver & get paid",
            icon = Icons.Rounded.LocalShipping,
            tintColor = Color(0xFFC2185B),
            bgColor = Color(0xFFFCE4EC)
        )
    )

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, SoftGreyBgEnd)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // App Logo
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .shadow(
                            elevation = 8.dp, // Slight elevation as requested
                            shape = RoundedCornerShape(20.dp),
                            spotColor = ElegantTeal.copy(alpha = 0.3f)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(ElegantTeal, ElegantTealDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "S",
                        color = Color.White,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "SAU Services",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    letterSpacing = 0.5.sp
                )

                Text(
                    text = "Choose your role to get started",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextGrey,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Role Grid Replacement with Row/Column for "Short" boxes and "Continue after boxes"
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            RoleCard(role = roles[0], isSelected = selectedRoleId == roles[0].id, onClick = { selectedRoleId = roles[0].id })
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            RoleCard(role = roles[1], isSelected = selectedRoleId == roles[1].id, onClick = { selectedRoleId = roles[1].id })
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            RoleCard(role = roles[2], isSelected = selectedRoleId == roles[2].id, onClick = { selectedRoleId = roles[2].id })
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            RoleCard(role = roles[3], isSelected = selectedRoleId == roles[3].id, onClick = { selectedRoleId = roles[3].id })
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Continue Button Section - Directly after boxes
                Button(
                    onClick = {
                        selectedRoleId?.let { role ->
                            navController.navigate(Screen.AuthOptions.createRoute(role))
                        }
                    },
                    enabled = selectedRoleId != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = if (selectedRoleId != null) 4.dp else 0.dp, // Slight elevation
                            shape = RoundedCornerShape(16.dp),
                            spotColor = ElegantTeal
                        ),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        disabledContainerColor = Color(0xFFE0E0E0)
                    ),
                    contentPadding = PaddingValues()
                ) {
                    val background = if (selectedRoleId != null) {
                        Brush.linearGradient(colors = listOf(ElegantTeal, ElegantTealDark))
                    } else {
                        Brush.linearGradient(colors = listOf(Color(0xFFE0E0E0), Color(0xFFE0E0E0)))
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(background),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Continue",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedRoleId != null) Color.White else Color(0xFF9E9E9E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Trust Indicators
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    TrustIndicator(text = "Trusted by 10K+", icon = Icons.Rounded.Star)
                    TrustIndicator(text = "Secure & Verified", icon = Icons.Rounded.Verified)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Legal Text
                val annotatedText = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = TextGrey)) {
                        append("By continuing, you agree to our ")
                    }
                    withStyle(style = SpanStyle(color = ElegantTeal, fontWeight = FontWeight.SemiBold)) {
                        append("Terms of Service")
                    }
                    withStyle(style = SpanStyle(color = TextGrey)) {
                        append(" & ")
                    }
                    withStyle(style = SpanStyle(color = ElegantTeal, fontWeight = FontWeight.SemiBold)) {
                        append("Privacy Policy")
                    }
                }

                Text(
                    text = annotatedText,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 16.dp, top = 0.dp, end = 16.dp, bottom = 24.dp)
                )
            }
        }
    }
}

@Composable
fun RoleCard(
    role: RoleOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.98f
            isSelected -> 1.02f
            else -> 1f
        },
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "scale"
    )

    // Using slight elevation values
    val elevation by animateDpAsState(
        targetValue = if (isSelected || isPressed) 6.dp else 2.dp,
        label = "elevation"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) ElegantTeal else Color.Transparent,
        label = "borderColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp) // Fixed shorter height
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(24.dp),
                spotColor = if (isSelected) ElegantTeal else Color.Black.copy(alpha = 0.1f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = ElegantTeal.copy(alpha = 0.1f)),
                onClick = onClick
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon Container
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(role.bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = role.icon,
                    contentDescription = role.title,
                    tint = role.tintColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = role.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = role.description,
                fontSize = 11.sp,
                color = TextGrey,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun TrustIndicator(text: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ElegantTeal,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextGrey)
    }
}
