package com.kris.aurelvio.domain.repository.fake

import com.kris.aurelvio.domain.model.Asset
import com.kris.aurelvio.domain.model.AssetId
import com.kris.aurelvio.domain.repository.AssetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeAssetRepository : AssetRepository {

    private val assetsFlow = MutableStateFlow<List<Asset>>(emptyList())

    override suspend fun addAsset(asset: Asset): AssetId {
        assetsFlow.value = assetsFlow.value + asset
        return asset.id
    }

    override fun observeAssets(): Flow<List<Asset>> {
        return assetsFlow.asStateFlow()
    }

    override suspend fun getAsset(id: AssetId): Asset? {
        return assetsFlow.value.find { it.id == id }
    }

    fun getAllAssets(): List<Asset> = assetsFlow.value
}
