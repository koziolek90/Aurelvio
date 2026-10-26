package com.kris.aurelvio.domain.usecase

import com.kris.aurelvio.core.common.result.Outcome
import com.kris.aurelvio.domain.model.Asset
import com.kris.aurelvio.domain.model.AssetDraft
import com.kris.aurelvio.domain.model.AssetId
import com.kris.aurelvio.domain.model.Entry
import com.kris.aurelvio.domain.model.EntryId
import com.kris.aurelvio.domain.model.Money
import com.kris.aurelvio.domain.repository.AssetRepository
import com.kris.aurelvio.domain.repository.EntryRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.uuid.Uuid

class AddAssetUseCase(
    private val assetRepository: AssetRepository,
    private val entryRepository: EntryRepository,
    private val clock: Clock = Clock.System,
    private val idGenerator: () -> String = { Uuid.random().toString() },
) {
    suspend operator fun invoke(
        draft: AssetDraft,
        entryDate: LocalDate? = null,
    ): Outcome<AssetId, AddAssetError> {
        if (draft.name.isBlank()) {
            return Outcome.Failure(AddAssetError.BlankName)
        }

        val initialValue = draft.initialValue
        if (initialValue != null && (initialValue < Money.ZERO)) {
            return Outcome.Failure(AddAssetError.NegativeInitialValue)
        }

        val assetId = AssetId(idGenerator())
        val asset = Asset(
            id = assetId,
            name = draft.name.trim(),
            categoryId = draft.categoryId,
            colorHex = draft.colorHex,
            isArchived = false,
        )

        assetRepository.addAsset(asset)

        if (initialValue != null && (initialValue > Money.ZERO)) {
            val date = entryDate ?: clock.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
            val netContribution = if (draft.initialValueAsContribution) {
                initialValue
            } else {
                Money.ZERO
            }

            val entry = Entry(
                id = EntryId(idGenerator()),
                assetId = assetId,
                date = date,
                value = initialValue,
                netContribution = netContribution,
                note = null,
            )
            entryRepository.addEntry(entry)
        }

        return Outcome.Success(assetId)
    }
}
