package com.yuba.cafe.model

data class UserProfile(
    val id: Long,
    val name: String,
    val email: String,
    val gender: String,
    val role: String
)
