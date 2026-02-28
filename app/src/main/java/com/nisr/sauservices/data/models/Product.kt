package com.nisr.sauservices.data.models

import androidx.annotation.DrawableRes

data class Product(
    val id: String,
    val name: String,
    @DrawableRes val image: Int,
    val rating: Double,
    val description: String,
    val price: Double,
    val unit: String,
    val productTypeId: String,
    val stock: Int
)
