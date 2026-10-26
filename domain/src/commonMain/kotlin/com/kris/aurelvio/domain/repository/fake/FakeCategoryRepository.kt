package com.kris.aurelvio.domain.repository.fake

import com.kris.aurelvio.domain.model.Category
import com.kris.aurelvio.domain.model.CategoryId
import com.kris.aurelvio.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeCategoryRepository(
    initialCategories: List<Category> = DEFAULT_CATEGORIES,
) : CategoryRepository {

    private val categoriesFlow = MutableStateFlow(initialCategories)

    override fun observeCategories(): Flow<List<Category>> {
        return categoriesFlow.asStateFlow()
    }

    companion object {
        val DEFAULT_CATEGORIES = listOf(
            Category(
                id = CategoryId("cat_accounts_deposits"),
                name = "Konta i lokaty",
                colorHex = "#4A90E2",
                iconName = "account_balance",
            ),
            Category(
                id = CategoryId("cat_investments"),
                name = "Inwestycje",
                colorHex = "#7ED321",
                iconName = "trending_up",
            ),
            Category(
                id = CategoryId("cat_bonds"),
                name = "Obligacje",
                colorHex = "#F5A623",
                iconName = "description",
            ),
            Category(
                id = CategoryId("cat_cash"),
                name = "Gotówka",
                colorHex = "#50E3C2",
                iconName = "payments",
            ),
        )
    }
}
