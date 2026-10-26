package com.kris.aurelvio.domain.repository

import com.kris.aurelvio.domain.model.AssetId
import com.kris.aurelvio.domain.model.Entry
import com.kris.aurelvio.domain.model.EntryId
import kotlinx.coroutines.flow.Flow

interface EntryRepository {
    suspend fun addEntry(entry: Entry): EntryId
    fun observeEntries(assetId: AssetId): Flow<List<Entry>>
}
