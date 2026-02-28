package com.nisr.sauservices.ui.viewmodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import com.nisr.sauservices.data.models.CategoryModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeUiState(
    val categories: List<CategoryModel> = emptyList(),
    val cartBadgeCount: Int = 2,
    val deliveryLocation: String = "Your Location"
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        val list = listOf(
            CategoryModel("1", "Residential Services", Icons.Default.HomeWork),
            CategoryModel("2", "Essential Supplies", Icons.Default.ShoppingCart),
            CategoryModel("3", "Home Essentials", Icons.Default.Kitchen),
            CategoryModel("4", "Food & Beverages", Icons.Default.Restaurant),
            CategoryModel("5", "Education Services", Icons.Default.School),
            CategoryModel("6", "Business & Professional", Icons.Default.BusinessCenter),
            CategoryModel("7", "Home & Lifestyle", Icons.Default.Weekend),
            CategoryModel("8", "Tech Services", Icons.Default.Phonelink),
            CategoryModel("9", "Men's Grooming", Icons.Default.ContentCut),
            CategoryModel("10", "Women's Beauty", Icons.Default.AutoFixHigh),
            CategoryModel("11", "Healthcare & Pharmacy", Icons.Default.LocalPharmacy)
        )
        _uiState.value = _uiState.value.copy(categories = list)
    }
}
