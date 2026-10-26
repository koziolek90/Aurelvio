package com.kris.aurelvio.domain.repository

import com.kris.aurelvio.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>
}
