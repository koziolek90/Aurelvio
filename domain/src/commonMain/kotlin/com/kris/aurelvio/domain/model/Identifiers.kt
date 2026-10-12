package com.kris.aurelvio.domain.model

import kotlin.jvm.JvmInline

@JvmInline
value class AssetId(val value: String) {
    init {
        require(value.isNotBlank()) { "AssetId cannot be blank" }
    }
}

@JvmInline
value class EntryId(val value: String) {
    init {
        require(value.isNotBlank()) { "EntryId cannot be blank" }
    }
}

@JvmInline
value class CategoryId(val value: String) {
    init {
        require(value.isNotBlank()) { "CategoryId cannot be blank" }
    }
}
