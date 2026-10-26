package com.kris.aurelvio.domain.usecase

sealed interface AddAssetError {
    data object BlankName : AddAssetError
    data object NegativeInitialValue : AddAssetError
}
