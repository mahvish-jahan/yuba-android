package com.yuba.cafe.ui.utils

import java.text.NumberFormat
import java.util.Locale

fun formatPrice(price: Long): String {
    return NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(price)
}
