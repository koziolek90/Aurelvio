package com.kris.aurelvio.domain.usecase

import com.kris.aurelvio.domain.model.Category
import com.kris.aurelvio.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

class GetCategoriesUseCase(
    private val categoryRepository: CategoryRepository,
) {
    operator fun invoke(): Flow<List<Category>> {
        return categoryRepository.observeCategories()
    }
}
