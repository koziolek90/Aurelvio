package com.kris.aurelvio.domain.model

import kotlinx.datetime.LocalDate

data class Entry(
    val id: EntryId,
    val assetId: AssetId,
    val date: LocalDate,
    val value: Money,
    val netContribution: Money = Money.ZERO,
    val note: String? = null,
)
