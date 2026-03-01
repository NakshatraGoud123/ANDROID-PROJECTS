package com.nisr.sauservices.ui.business

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.Screen
import com.nisr.sauservices.ui.theme.PinkPrimary
import com.nisr.sauservices.ui.theme.LightPink
import com.nisr.sauservices.ui.viewmodel.BusinessViewModel
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessServicesScreen(navController: NavController, subcategory: String, viewModel: BusinessViewModel) {
    val decodedSub = URLDecoder.decode(subcategory, "UTF-8")
    
    val services = getServicesForSubcategory(decodedSub)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(decodedSub, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.BusinessCart.route) }) {
                        BadgedBox(badge = {
                            if (viewModel.cartItems.isNotEmpty()) {
                                Badge(containerColor = PinkPrimary) {
                                    Text(viewModel.cartItems.sumOf { it.quantity }.toString(), color = Color.White)
                                }
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF7F7F7)
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(services) { service ->
                BusinessServiceCard(service, viewModel)
            }
        }
    }
}

@Composable
fun BusinessServiceCard(service: BusinessService, viewModel: BusinessViewModel) {
    val cartItem = viewModel.cartItems.find { it.id == service.id }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = service.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "₹${service.price}", fontWeight = FontWeight.Bold, color = PinkPrimary, fontSize = 14.sp)
            }

            if (cartItem == null) {
                Button(
                    onClick = { viewModel.addToCart(service) },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("ADD", fontWeight = FontWeight.Bold)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(LightPink)
                ) {
                    IconButton(onClick = { viewModel.decreaseQty(service.id) }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Remove, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(18.dp))
                    }
                    Text(
                        text = cartItem.quantity.toString(),
                        fontWeight = FontWeight.Bold,
                        color = PinkPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(onClick = { viewModel.increaseQty(service.id) }, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = PinkPrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

fun getServicesForSubcategory(sub: String): List<BusinessService> {
    return when (sub) {
        // IT Support
        "Computer Repair" -> listOf(
            BusinessService("it_1", "Laptop Repair", 800.0, "IT Support", "Computer Repair"),
            BusinessService("it_2", "Desktop Repair", 700.0, "IT Support", "Computer Repair"),
            BusinessService("it_3", "OS Installation", 500.0, "IT Support", "Computer Repair"),
            BusinessService("it_4", "Virus Removal", 400.0, "IT Support", "Computer Repair")
        )
        "Network Setup" -> listOf(
            BusinessService("it_5", "WiFi Installation", 600.0, "IT Support", "Network Setup"),
            BusinessService("it_6", "Router Setup", 500.0, "IT Support", "Network Setup"),
            BusinessService("it_7", "Office Network Setup", 2000.0, "IT Support", "Network Setup")
        )
        "Software Services" -> listOf(
            BusinessService("it_8", "Software Installation", 300.0, "IT Support", "Software Services"),
            BusinessService("it_9", "Data Backup", 500.0, "IT Support", "Software Services"),
            BusinessService("it_10", "System Upgrade", 900.0, "IT Support", "Software Services")
        )
        "AMC Services" -> listOf(
            BusinessService("it_11", "Monthly Maintenance", 1500.0, "IT Support", "AMC Services"),
            BusinessService("it_12", "Yearly Maintenance", 8000.0, "IT Support", "AMC Services")
        )
        // Marketing
        "Digital Marketing" -> listOf(
            BusinessService("mkt_1", "SEO Optimization", 5000.0, "Marketing", "Digital Marketing"),
            BusinessService("mkt_2", "Social Media Marketing", 4000.0, "Marketing", "Digital Marketing"),
            BusinessService("mkt_3", "Google Ads Setup", 6000.0, "Marketing", "Digital Marketing")
        )
        "Design Services" -> listOf(
            BusinessService("mkt_4", "Logo Design", 1500.0, "Marketing", "Design Services"),
            BusinessService("mkt_5", "Poster Design", 800.0, "Marketing", "Design Services"),
            BusinessService("mkt_6", "Social Media Post Design", 1000.0, "Marketing", "Design Services")
        )
        "Branding" -> listOf(
            BusinessService("mkt_7", "Business Branding Kit", 7000.0, "Marketing", "Branding"),
            BusinessService("mkt_8", "Website Branding", 5000.0, "Marketing", "Branding")
        )
        "Video Promotion" -> listOf(
            BusinessService("mkt_9", "Promo Video Creation", 3000.0, "Marketing", "Video Promotion"),
            BusinessService("mkt_10", "Ad Video Editing", 2000.0, "Marketing", "Video Promotion")
        )
        // Accounting
        "Bookkeeping" -> listOf(
            BusinessService("acc_1", "Monthly Bookkeeping", 2000.0, "Accounting", "Bookkeeping"),
            BusinessService("acc_2", "Annual Bookkeeping", 15000.0, "Accounting", "Bookkeeping")
        )
        "GST Services" -> listOf(
            BusinessService("acc_3", "GST Registration", 1500.0, "Accounting", "GST Services"),
            BusinessService("acc_4", "GST Filing", 1000.0, "Accounting", "GST Services")
        )
        "Tax Services" -> listOf(
            BusinessService("acc_5", "Income Tax Filing", 2000.0, "Accounting", "Tax Services"),
            BusinessService("acc_6", "Business Tax Filing", 5000.0, "Accounting", "Tax Services")
        )
        "Payroll" -> listOf(
            BusinessService("acc_7", "Salary Processing", 1500.0, "Accounting", "Payroll"),
            BusinessService("acc_8", "Employee Payslips", 500.0, "Accounting", "Payroll")
        )
        // Legal
        "Business Registration" -> listOf(
            BusinessService("leg_1", "MSME Registration", 1000.0, "Legal", "Business Registration"),
            BusinessService("leg_2", "Company Registration", 8000.0, "Legal", "Business Registration"),
            BusinessService("leg_3", "Partnership Registration", 5000.0, "Legal", "Business Registration")
        )
        "Legal Drafting" -> listOf(
            BusinessService("leg_4", "Agreement Drafting", 2000.0, "Legal", "Legal Drafting"),
            BusinessService("leg_5", "Contract Drafting", 3000.0, "Legal", "Legal Drafting")
        )
        "Consultation" -> listOf(
            BusinessService("leg_6", "Legal Advice (30 min)", 500.0, "Legal", "Consultation"),
            BusinessService("leg_7", "Legal Advice (1 hr)", 900.0, "Legal", "Consultation")
        )
        "Documentation" -> listOf(
            BusinessService("leg_8", "Affidavit", 300.0, "Legal", "Documentation"),
            BusinessService("leg_9", "Notary Services", 200.0, "Legal", "Documentation")
        )
        // Printing
        "Office Printing" -> listOf(
            BusinessService("prt_1", "Black & White Printing", 2.0, "Printing", "Office Printing"),
            BusinessService("prt_2", "Color Printing", 5.0, "Printing", "Office Printing")
        )
        "Marketing Prints" -> listOf(
            BusinessService("prt_3", "Visiting Cards (100 pcs)", 250.0, "Printing", "Marketing Prints"),
            BusinessService("prt_4", "Flyers (100 pcs)", 400.0, "Printing", "Marketing Prints"),
            BusinessService("prt_5", "Posters", 100.0, "Printing", "Marketing Prints")
        )
        "Custom Prints" -> listOf(
            BusinessService("prt_6", "Flex Printing", 40.0, "Printing", "Custom Prints"),
            BusinessService("prt_7", "Banner Printing", 50.0, "Printing", "Custom Prints")
        )
        "Binding" -> listOf(
            BusinessService("prt_8", "Spiral Binding", 60.0, "Printing", "Binding"),
            BusinessService("prt_9", "Hard Binding", 120.0, "Printing", "Binding")
        )
        // Courier
        "Local Delivery" -> listOf(
            BusinessService("cou_1", "Same Day Delivery", 80.0, "Courier", "Local Delivery"),
            BusinessService("cou_2", "Next Day Delivery", 50.0, "Courier", "Local Delivery")
        )
        "Domestic Courier" -> listOf(
            BusinessService("cou_3", "Standard Delivery", 120.0, "Courier", "Domestic Courier"),
            BusinessService("cou_4", "Express Delivery", 200.0, "Courier", "Domestic Courier")
        )
        "International Courier" -> listOf(
            BusinessService("cou_5", "Documents", 1500.0, "Courier", "International Courier"),
            BusinessService("cou_6", "Parcels", 2500.0, "Courier", "International Courier")
        )
        "Business Shipping" -> listOf(
            BusinessService("cou_7", "Bulk Courier Service", 5000.0, "Courier", "Business Shipping"),
            BusinessService("cou_8", "Monthly Courier Plan", 8000.0, "Courier", "Business Shipping")
        )
        else -> emptyList()
    }
}
