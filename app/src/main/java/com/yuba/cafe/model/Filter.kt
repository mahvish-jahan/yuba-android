package com.yuba.cafe.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector

@Stable
class Filter(
    val name: String,
    enabled: Boolean = false,
    val icon: ImageVector? = null
) {
    val enabled = mutableStateOf(enabled)
}

val filters = listOf(
    com.yuba.cafe.model.Filter(name = "Organic"),
    com.yuba.cafe.model.Filter(name = "Gluten-free"),
    com.yuba.cafe.model.Filter(name = "Dairy-free"),
    com.yuba.cafe.model.Filter(name = "Sweet"),
    com.yuba.cafe.model.Filter(name = "Savory")
)
val priceFilters = listOf(
    com.yuba.cafe.model.Filter(name = "$"),
    com.yuba.cafe.model.Filter(name = "$$"),
    com.yuba.cafe.model.Filter(name = "$$$"),
    com.yuba.cafe.model.Filter(name = "$$$$")
)
val sortFilters = listOf(
    com.yuba.cafe.model.Filter(
        name = "Android's favorite (default)",
        icon = Icons.Filled.Android
    ),
    com.yuba.cafe.model.Filter(name = "Rating", icon = Icons.Filled.Star),
    com.yuba.cafe.model.Filter(name = "Alphabetical", icon = Icons.Filled.SortByAlpha)
)

val categoryFilters = listOf(
    com.yuba.cafe.model.Filter(name = "Chips & crackers"),
    com.yuba.cafe.model.Filter(name = "Fruit snacks"),
    com.yuba.cafe.model.Filter(name = "Desserts"),
    com.yuba.cafe.model.Filter(name = "Nuts")
)
val lifeStyleFilters = listOf(
    com.yuba.cafe.model.Filter(name = "Organic"),
    com.yuba.cafe.model.Filter(name = "Gluten-free"),
    com.yuba.cafe.model.Filter(name = "Dairy-free"),
    com.yuba.cafe.model.Filter(name = "Sweet"),
    com.yuba.cafe.model.Filter(name = "Savory")
)

var sortDefault = com.yuba.cafe.model.sortFilters.get(0).name
