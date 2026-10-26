package com.kris.aurelvio.domain.model

import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class DomainModelsTest {

    @Test
    fun testCategoryCreation() {
        val category = Category(
            id = CategoryId("cat_investments"),
            name = "Inwestycje",
            colorHex = "#F59E0B",
            iconName = "ic_trending_up",
        )

        assertEquals("cat_investments", category.id.value)
        assertEquals("Inwestycje", category.name)
        assertEquals("#F59E0B", category.colorHex)
        assertEquals("ic_trending_up", category.iconName)
    }

    @Test
    fun testAssetCreationAndDefaultArchivedStatus() {
        val asset = Asset(
            id = AssetId("asset_1"),
            name = "ETF MSCI World",
            categoryId = CategoryId("cat_investments"),
            colorHex = "#F59E0B",
        )

        assertEquals("asset_1", asset.id.value)
        assertFalse(asset.isArchived)
    }

    @Test
    fun testEntryCreationWithDefaults() {
        val entry = Entry(
            id = EntryId("entry_100"),
            assetId = AssetId("asset_1"),
            date = LocalDate(2025, 2, 22),
            value = Money(10_000L),
        )

        assertEquals(Money.ZERO, entry.netContribution)
        assertNull(entry.note)
        assertEquals(Money(10_000L), entry.value)
    }

    @Test
    fun testAssetDraftAndEntryDraftDefaults() {
        val assetDraft = AssetDraft(
            name = "Konto Oszczędnościowe Pekao",
            categoryId = CategoryId("cat_savings"),
            colorHex = "#3B82F6",
        )

        assertNull(assetDraft.initialValue)
        assertTrue(assetDraft.initialValueAsContribution)

        val entryDraft = EntryDraft(
            assetId = AssetId("asset_2"),
            date = LocalDate(2025, 2, 22),
            value = Money(5_000L),
            netContribution = Money(2_000L),
            note = "Depozyt początkowy",
        )

        assertEquals(Money(2_000L), entryDraft.netContribution)
        assertEquals("Depozyt początkowy", entryDraft.note)
    }
}
