package com.kris.aurelvio.domain.usecase

import com.kris.aurelvio.core.common.result.Outcome
import com.kris.aurelvio.domain.model.AssetDraft
import com.kris.aurelvio.domain.model.CategoryId
import com.kris.aurelvio.domain.model.Money
import com.kris.aurelvio.domain.repository.fake.FakeAssetRepository
import com.kris.aurelvio.domain.repository.fake.FakeEntryRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AddAssetUseCaseTest {

    private val assetRepository = FakeAssetRepository()
    private val entryRepository = FakeEntryRepository()

    private var idCounter = 0
    private val useCase = AddAssetUseCase(
        assetRepository = assetRepository,
        entryRepository = entryRepository,
        idGenerator = { "test_id_${++idCounter}" },
    )

    @Test
    fun invoke_validDraft_withoutInitialValue_addsAssetWithoutEntry() = runTest {
        val draft = AssetDraft(
            name = "Konto Pekao",
            categoryId = CategoryId("cat_accounts_deposits"),
            colorHex = "#4A90E2",
            initialValue = null,
        )

        val result = useCase(draft)

        assertTrue(result is Outcome.Success)
        val assets = assetRepository.getAllAssets()
        assertEquals(1, assets.size)
        assertEquals("Konto Pekao", assets.first().name)
        assertEquals(CategoryId("cat_accounts_deposits"), assets.first().categoryId)
        assertEquals("#4A90E2", assets.first().colorHex)

        val entries = entryRepository.getAllEntries()
        assertTrue(entries.isEmpty())
    }

    @Test
    fun invoke_validDraft_withInitialValueAsContribution_addsAssetAndEntry() = runTest {
        val draft = AssetDraft(
            name = "Obligacje skarbowe",
            categoryId = CategoryId("cat_bonds"),
            colorHex = "#F5A623",
            initialValue = Money(10_000L),
            initialValueAsContribution = true,
        )
        val entryDate = LocalDate(2025, 3, 1)

        val result = useCase(draft, entryDate = entryDate)

        assertTrue(result is Outcome.Success)
        val assets = assetRepository.getAllAssets()
        assertEquals(1, assets.size)

        val entries = entryRepository.getAllEntries()
        assertEquals(1, entries.size)
        val entry = entries.first()
        assertEquals(assets.first().id, entry.assetId)
        assertEquals(entryDate, entry.date)
        assertEquals(Money(10_000L), entry.value)
        assertEquals(Money(10_000L), entry.netContribution)
    }

    @Test
    fun invoke_validDraft_withInitialValueNotAsContribution_addsAssetAndEntryWithZeroContribution() = runTest {
        val draft = AssetDraft(
            name = "Spadek / Darowizna",
            categoryId = CategoryId("cat_cash"),
            colorHex = "#50E3C2",
            initialValue = Money(5_000L),
            initialValueAsContribution = false,
        )
        val entryDate = LocalDate(2025, 3, 1)

        val result = useCase(draft, entryDate = entryDate)

        assertTrue(result is Outcome.Success)
        val entries = entryRepository.getAllEntries()
        assertEquals(1, entries.size)
        val entry = entries.first()
        assertEquals(Money(5_000L), entry.value)
        assertEquals(Money.ZERO, entry.netContribution)
    }

    @Test
    fun invoke_blankName_returnsBlankNameError() = runTest {
        val draft = AssetDraft(
            name = "   ",
            categoryId = CategoryId("cat_cash"),
            colorHex = "#50E3C2",
        )

        val result = useCase(draft)

        assertEquals(Outcome.Failure(AddAssetError.BlankName), result)
        assertTrue(assetRepository.getAllAssets().isEmpty())
        assertTrue(entryRepository.getAllEntries().isEmpty())
    }

    @Test
    fun invoke_negativeInitialValue_returnsNegativeInitialValueError() = runTest {
        val draft = AssetDraft(
            name = "Test",
            categoryId = CategoryId("cat_cash"),
            colorHex = "#50E3C2",
            initialValue = Money(-500L),
        )

        val result = useCase(draft)

        assertEquals(Outcome.Failure(AddAssetError.NegativeInitialValue), result)
        assertTrue(assetRepository.getAllAssets().isEmpty())
        assertTrue(entryRepository.getAllEntries().isEmpty())
    }

    @Test
    fun invoke_zeroInitialValue_addsAssetWithoutEntry() = runTest {
        val draft = AssetDraft(
            name = "Puste konto",
            categoryId = CategoryId("cat_accounts_deposits"),
            colorHex = "#4A90E2",
            initialValue = Money.ZERO,
        )

        val result = useCase(draft)

        assertTrue(result is Outcome.Success)
        assertEquals(1, assetRepository.getAllAssets().size)
        assertTrue(entryRepository.getAllEntries().isEmpty())
    }
}
