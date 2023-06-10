package com.yuba.cafe.model.order

import com.yuba.cafe.model.Address

data class Order(
    val id: Long,
    val totalAmount: Double,
    val address: Address,
    val orderItems: List<OrderItem>,
    val orderState: OrderState
)