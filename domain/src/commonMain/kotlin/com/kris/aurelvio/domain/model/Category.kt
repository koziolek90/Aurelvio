package com.kris.aurelvio.domain.model

data class Category(
    val id: CategoryId,
    val name: String,
    val colorHex: String,
    val iconName: String? = null,
)