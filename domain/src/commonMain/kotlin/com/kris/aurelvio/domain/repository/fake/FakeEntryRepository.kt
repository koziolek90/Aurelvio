package com.kris.aurelvio.domain.repository.fake

import com.kris.aurelvio.domain.model.AssetId
import com.kris.aurelvio.domain.model.Entry
import com.kris.aurelvio.domain.model.EntryId
import com.kris.aurelvio.domain.repository.EntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeEntryRepository : EntryRepository {

    private val entriesFlow = MutableStateFlow<List<Entry>>(emptyList())

    override suspend fun addEntry(entry: Entry): EntryId {
        entriesFlow.value = entriesFlow.value + entry
        return entry.id
    }

    override fun observeEntries(assetId: AssetId): Flow<List<Entry>> {
        return entriesFlow.map { entries ->
            entries.filter { it.assetId == assetId }
        }
    }

    fun getAllEntries(): List<Entry> = entriesFlow.value
}
