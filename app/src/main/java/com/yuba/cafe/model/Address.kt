package com.yuba.cafe.model

data class Address(
    val id: Long,
    val name: String,
    val addressDetail: String,
    val pinCode: Long,
    val isCurrent: Boolean,
    val userId: Long
)
