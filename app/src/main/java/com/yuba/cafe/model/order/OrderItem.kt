package com.yuba.cafe.model.order

import com.yuba.cafe.model.Snack

data class OrderItem(
    val id: Long,
    val snack: Snack,
    val count: Long
)
