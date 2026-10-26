package com.kris.aurelvio.domain.repository

import com.kris.aurelvio.domain.model.Asset
import com.kris.aurelvio.domain.model.AssetId
import kotlinx.coroutines.flow.Flow

interface AssetRepository {
    suspend fun addAsset(asset: Asset): AssetId
    fun observeAssets(): Flow<List<Asset>>
    suspend fun getAsset(id: AssetId): Asset?
}
