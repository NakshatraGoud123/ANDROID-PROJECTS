package com.nisr.sauservices.ui.profile

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.nisr.sauservices.ui.theme.PinkPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAQScreen(navController: NavController) {
    val faqs = listOf(
        FAQItem(
            "How do I book a service?",
            "To book a service, select a category from the home screen, choose your required service, select date and time, confirm your address, and complete the payment. You will receive a booking confirmation notification instantly."
        ),
        FAQItem(
            "How can I track my order or service?",
            "Go to “My Orders” in your profile section. You can see live status updates such as Accepted, In Progress, Out for Delivery, or Completed."
        ),
        FAQItem(
            "What is the cancellation policy?",
            "You can cancel a booking before the service provider starts the service. If canceled early, no charges apply. If canceled after provider dispatch, a small cancellation fee may be deducted."
        ),
        FAQItem(
            "What is the refund policy?",
            "Refunds are processed in the following cases:\n• Service not delivered\n• Payment deducted but booking failed\n• Verified service issues\nRefunds are credited to the original payment method within 3–7 working days."
        ),
        FAQItem(
            "How do I reschedule a booking?",
            "Open your booking details under “My Orders,” click “Reschedule,” choose a new date and time, and confirm."
        ),
        FAQItem(
            "What payment methods are supported?",
            "We support: UPI, Debit Card, Credit Card, Net Banking, Wallets, and Cash on Delivery (selected services). All online transactions are encrypted and secure."
        ),
        FAQItem(
            "How can I contact customer support?",
            "Go to the “Contact Us” page in your profile and submit a support request form, or use the provided support email and phone number during working hours."
        ),
        FAQItem(
            "How can I join as a Worker, Shopkeeper, or Delivery Partner?",
            "During signup, select your desired role. Complete profile verification and upload required documents. Once approved, you can start receiving and accepting orders."
        )
    )

    var expandedIndex by remember { mutableIntStateOf(-1) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FAQ", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(faqs) { index, faq ->
                FAQCard(
                    faq = faq,
                    isExpanded = expandedIndex == index,
                    onToggle = {
                        expandedIndex = if (expandedIndex == index) -1 else index
                    }
                )
            }
        }
    }
}

@Composable
fun FAQCard(faq: FAQItem, isExpanded: Boolean, onToggle: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9F9F9),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = faq.question,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f),
                    color = Color.Black
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = PinkPrimary
                )
            }
            
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = faq.answer,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

data class FAQItem(val question: String, val answer: String)
