package com.nisr.sauservices.ui.pls

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.nisr.sauservices.data.local.SessionManager
import com.nisr.sauservices.data.model.PLSBooking
import com.nisr.sauservices.data.model.PLSService
import com.nisr.sauservices.navigation.Routes
import com.nisr.sauservices.ui.viewmodels.PropertyLifestyleViewModel
import java.util.*

private val PrimaryBlue = Color(0xFF1E3A8A)
private val Background = Color(0xFFF9FAFB)
private val Surface = Color(0xFFFFFFFF)
private val Border = Color(0xFFE5E7EB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PLSMainScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Property & Lifestyle", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        containerColor = Background
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            Text("Our Premium Services", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            
            val categories = listOf(
                "Maid & Domestic Help" to Icons.Rounded.Person,
                "Security Services" to Icons.Rounded.Security,
                "Interior Designing" to Icons.Rounded.DesignServices,
                "Exterior & Outdoor" to Icons.Rounded.HomeWork,
                "Event Venues" to Icons.Rounded.Event,
                "Resorts & Stays" to Icons.Rounded.BeachAccess
            )

            categories.forEach { (name, icon) ->
                CategoryCard(name, icon) {
                    navController.navigate("PLS_subcategories/$name")
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun CategoryCard(name: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Border),
        color = Surface
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(PrimaryBlue.copy(0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = PrimaryBlue)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Rounded.ChevronRight, null, tint = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PLSSubcategoriesScreen(navController: NavController, category: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            val subcategories = when (category) {
                "Maid & Domestic Help" -> listOf("Maid Services")
                "Security Services" -> listOf("Security")
                "Interior Designing" -> listOf("Interiors")
                "Exterior & Outdoor" -> listOf("Exteriors")
                "Event Venues" -> listOf("Function Halls")
                "Resorts & Stays" -> listOf("Resorts")
                else -> emptyList()
            }

            subcategories.forEach { sub ->
                CategoryCard(sub, Icons.Rounded.Layers) {
                    navController.navigate("PLS_services/$sub")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PLSServicesScreen(navController: NavController, subcategory: String, viewModel: PropertyLifestyleViewModel) {
    LaunchedEffect(subcategory) { viewModel.loadServices(subcategory) }
    val services by viewModel.services.collectAsState()

    val displayServices = if (services.isEmpty()) getStaticServices(subcategory) else services

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(subcategory) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            items(displayServices) { service ->
                ServiceItemCard(service) {
                    navController.navigate("PLS_booking/${service.id.ifEmpty { service.name }}")
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ServiceItemCard(service: PLSService, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Border),
        color = Surface
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(service.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Premium quality guaranteed", fontSize = 12.sp, color = Color.Gray)
            }
            Text("₹${service.price}${service.unit}", fontWeight = FontWeight.ExtraBold, color = PrimaryBlue)
        }
    }
}

fun getStaticServices(sub: String): List<PLSService> {
    return when(sub) {
        "Maid Services" -> listOf(
            PLSService("m1", "Part-Time Maid", 3000.0, "/month"),
            PLSService("m2", "Full-Time Maid", 8000.0, "/month"),
            PLSService("m3", "24H Maid", 15000.0, "/month"),
            PLSService("m4", "Cooking Maid", 5000.0, "/month")
        )
        "Security" -> listOf(
            PLSService("s1", "Home Guard", 9000.0, "/month"),
            PLSService("s2", "Apartment Watchman", 12000.0, "/month"),
            PLSService("s3", "Event Security", 18000.0, "/day")
        )
        "Interiors" -> listOf(
            PLSService("i1", "1BHK Design", 150000.0, ""),
            PLSService("i2", "2BHK Design", 300000.0, ""),
            PLSService("i3", "Kitchen Renovation", 120000.0, "")
        )
        "Exteriors" -> listOf(
            PLSService("e1", "House Exterior", 80000.0, ""),
            PLSService("e2", "Garden Setup", 50000.0, "")
        )
        "Function Halls" -> listOf(
            PLSService("h1", "Medium Hall", 25000.0, "/day"),
            PLSService("h2", "Luxury Hall", 120000.0, "/day")
        )
        "Resorts" -> listOf(
            PLSService("r1", "Budget Resort", 2000.0, "/night"),
            PLSService("r2", "Luxury Resort", 7000.0, "/night")
        )
        else -> emptyList()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PLSBookingScreen(
    navController: NavController, 
    serviceId: String, 
    viewModel: PropertyLifestyleViewModel,
    sessionManager: SessionManager
) {
    val service = viewModel.services.value.find { it.id == serviceId || it.name == serviceId } 
                ?: getStaticServices("Maid Services").find { it.id == serviceId || it.name == serviceId }
                ?: getStaticServices("Security").find { it.id == serviceId || it.name == serviceId }
                ?: getStaticServices("Interiors").find { it.id == serviceId || it.name == serviceId }
                ?: getStaticServices("Exteriors").find { it.id == serviceId || it.name == serviceId }
                ?: getStaticServices("Function Halls").find { it.id == serviceId || it.name == serviceId }
                ?: getStaticServices("Resorts").find { it.id == serviceId || it.name == serviceId }
                ?: PLSService(name = serviceId, price = 0.0)

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var timeSlot by remember { mutableStateOf("") }
    
    var guests by remember { mutableStateOf("1") }
    var duration by remember { mutableStateOf("1") }
    var area by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Booking Details") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Phone Number") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Full Address") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            
            OutlinedTextField(
                value = date,
                onValueChange = { },
                label = { Text("Select Date") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth().clickable {
                    DatePickerDialog(context, { _, y, m, d -> date = "$d/${m+1}/$y" }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                },
                trailingIcon = { Icon(Icons.Rounded.CalendarToday, null) }
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = timeSlot,
                onValueChange = { },
                label = { Text("Select Time Slot") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth().clickable {
                    TimePickerDialog(context, { _, h, min -> timeSlot = String.format("%02d:%02d", h, min) }, 10, 0, false).show()
                },
                trailingIcon = { Icon(Icons.Rounded.AccessTime, null) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            val sub = service.subcategory.lowercase()
            if (sub.contains("hall") || sub.contains("resort")) {
                OutlinedTextField(value = guests, onValueChange = { guests = it }, label = { Text("Number of Guests") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (Days/Nights)") }, modifier = Modifier.fillMaxWidth())
            } else if (sub.contains("maid") || sub.contains("security")) {
                OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (Months)") }, modifier = Modifier.fillMaxWidth())
            } else if (sub.contains("interior") || sub.contains("exterior")) {
                OutlinedTextField(value = area, onValueChange = { area = it }, label = { Text("Area (sqft)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(value = notes, onValueChange = { notes = it }, label = { Text("Specific Requirements") }, modifier = Modifier.fillMaxWidth())
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.length >= 10 && address.isNotBlank() && date.isNotBlank()) {
                        val booking = PLSBooking(
                            userId = FirebaseAuth.getInstance().currentUser?.uid ?: "",
                            userName = name,
                            userPhone = phone,
                            userAddress = address,
                            serviceName = service.name,
                            date = date,
                            timeSlot = timeSlot,
                            totalPrice = service.price * (duration.toDoubleOrNull() ?: 1.0),
                            guestsCount = guests.toIntOrNull(),
                            duration = duration.toIntOrNull(),
                            areaSqft = area.toDoubleOrNull(),
                            requirements = notes
                        )
                        viewModel.setCurrentBooking(booking)
                        navController.navigate(Routes.PLS_CHECKOUT)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Proceed to Checkout")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PLSCheckoutScreen(navController: NavController, viewModel: PropertyLifestyleViewModel) {
    val booking by viewModel.currentBooking.collectAsState()
    var paymentMethod by remember { mutableStateOf("Cash") }

    booking?.let { b ->
        Scaffold(
            topBar = { TopAppBar(title = { Text("Checkout") }) }
        ) { padding ->
            Column(modifier = Modifier.padding(padding).padding(16.dp)) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Border),
                    color = Surface
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(b.serviceName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("${b.date} • ${b.timeSlot}", color = Color.Gray)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Base Price")
                            Text("₹${b.totalPrice}")
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Service Fee")
                            Text("₹0.0")
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("₹${b.totalPrice}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = PrimaryBlue)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Text("Select Payment Method", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                
                listOf("Cash", "UPI", "Card").forEach { method ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { paymentMethod = method }.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = paymentMethod == method, onClick = { paymentMethod = method })
                        Text(method)
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { viewModel.placeBooking(b.copy(paymentMethod = paymentMethod)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Confirm Booking")
                }
            }
        }
    }

    val result by viewModel.bookingResult.collectAsState()
    LaunchedEffect(result) {
        if (result?.isSuccess == true) {
            navController.navigate(Routes.PLS_SUCCESS) {
                popUpTo(Routes.HOME) { inclusive = false }
            }
            viewModel.resetBookingResult()
        }
    }
}

@Composable
fun PLSSuccessScreen(navController: NavController, viewModel: PropertyLifestyleViewModel) {
    val booking by viewModel.currentBooking.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Rounded.CheckCircle, null, tint = Color(0xFF22C55E), modifier = Modifier.size(100.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text("Booking Confirmed!", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Booking ID: PLS-${(10000..99999).random()}", color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Our service provider will contact you shortly to confirm the details.",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = { navController.navigate(Routes.HOME) { popUpTo(0) } },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Text("Go to Home")
        }
    }
}
