package com.kris.aurelvio.domain.model

data class Asset(
    val id: AssetId,
    val name: String,
    val categoryId: CategoryId,
    val colorHex: String,
    val isArchived: Boolean = false,
)