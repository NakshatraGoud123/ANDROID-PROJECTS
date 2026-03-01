package com.nisr.sauservices.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.nisr.sauservices.ui.theme.PinkPrimary

data class CategoryItem(val name: String, val icon: ImageVector, val route: String = "")

@Composable
fun CategoriesGrid(
    navController: NavController, 
    showAll: Boolean = false,
    onHomeEssentialsClick: () -> Unit = {},
    onEducationClick: () -> Unit = {},
    onBusinessClick: () -> Unit = {},
    onLifestyleClick: () -> Unit = {},
    onTechClick: () -> Unit = {}
) {
    val allCategories = listOf(
        CategoryItem("Residential Services", Icons.Outlined.Home, Screen.ResidentialCategories.route),
        CategoryItem("Essential Supplies", Icons.Outlined.ShoppingBag),
        CategoryItem("Home Essentials", Icons.Outlined.Star),
        CategoryItem("Food & Beverages", Icons.Outlined.Restaurant),
        CategoryItem("Education Services", Icons.Outlined.School),
        CategoryItem("Business & Professional", Icons.Outlined.BusinessCenter),
        CategoryItem("Home & Lifestyle", Icons.Outlined.Lightbulb),
        CategoryItem("Tech Services", Icons.Outlined.LaptopMac),
        CategoryItem("Men's Grooming", Icons.Outlined.Face),
        CategoryItem("Women's Beauty", Icons.Outlined.AutoAwesome),
        CategoryItem("Healthcare & Pharmacy", Icons.Outlined.FavoriteBorder),
        CategoryItem("More Services", Icons.Outlined.GridView, Screen.Categories.route)
    )

    // In horizontal mode (not showAll), we show first 5 + More Services
    val displayList = if (showAll) {
        allCategories.filter { it.name != "More Services" }
    } else {
        allCategories.take(5) + allCategories.last()
    }

    Column(modifier = Modifier.padding(top = 16.dp)) {
        if (!showAll) {
            Text(
                "Categories",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black,
                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
            )
        }

        displayList.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEach { item ->
                    CategoryCard(
                        item = item, 
                        modifier = Modifier.weight(1f),
                        onClick = {
                            when (item.name) {
                                "More Services" -> navController.navigate(Screen.Categories.route)
                                "Home Essentials", "Essential Supplies" -> onHomeEssentialsClick()
                                "Residential Services" -> navController.navigate(item.route)
                                "Food & Beverages" -> navController.navigate("FOODS_categories")
                                "Education Services" -> onEducationClick()
                                "Business & Professional" -> onBusinessClick()
                                "Home & Lifestyle" -> onLifestyleClick()
                                "Tech Services" -> onTechClick()
                                "Men's Grooming" -> navController.navigate(Screen.MensCategories.route)
                                "Women's Beauty" -> navController.navigate(Screen.WomensBeautyCategories.route)
                                "Healthcare & Pharmacy" -> navController.navigate(Screen.HealthcareCategories.route)
                                else -> { /* Handle other categories */ }
                            }
                        }
                    )
                }
                repeat(3 - rowItems.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
fun CategoryCard(item: CategoryItem, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8F8F8))
            .clickable { onClick() }
            .padding(vertical = 16.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            item.icon, 
            null, 
            tint = PinkPrimary,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            item.name, 
            fontSize = 11.sp, 
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp,
            color = Color.DarkGray
        )
    }
}
