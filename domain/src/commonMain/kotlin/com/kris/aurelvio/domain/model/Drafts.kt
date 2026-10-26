package com.kris.aurelvio.domain.model

import kotlinx.datetime.LocalDate

data class AssetDraft(
    val name: String,
    val categoryId: CategoryId,
    val colorHex: String,
    val initialValue: Money? = null,
    val initialValueAsContribution: Boolean = true,
)

data class EntryDraft(
    val assetId: AssetId,
    val date: LocalDate,
    val value: Money,
    val netContribution: Money = Money.ZERO,
    val note: String? = null,
)
