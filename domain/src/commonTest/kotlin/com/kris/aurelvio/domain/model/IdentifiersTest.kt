package com.kris.aurelvio.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IdentifiersTest {

    @Test
    fun testValidIdentifiers() {
        val assetId = AssetId("asset_123")
        assertEquals("asset_123", assetId.value)

        val entryId = EntryId("entry_456")
        assertEquals("entry_456", entryId.value)

        val categoryId = CategoryId("cat_789")
        assertEquals("cat_789", categoryId.value)
    }

    @Test
    fun testBlankAssetIdThrowsException() {
        assertFailsWith<IllegalArgumentException> {
            AssetId("   ")
        }
    }

    @Test
    fun testBlankEntryIdThrowsException() {
        assertFailsWith<IllegalArgumentException> {
            EntryId("")
        }
    }

    @Test
    fun testBlankCategoryIdThrowsException() {
        assertFailsWith<IllegalArgumentException> {
            CategoryId("\t\n")
        }
    }
}
