package com.kris.aurelvio.feature.assets.presentation

import app.cash.turbine.test
import com.kris.aurelvio.domain.model.Category
import com.kris.aurelvio.domain.model.CategoryId
import com.kris.aurelvio.domain.model.Money
import com.kris.aurelvio.domain.repository.fake.FakeAssetRepository
import com.kris.aurelvio.domain.repository.fake.FakeCategoryRepository
import com.kris.aurelvio.domain.repository.fake.FakeEntryRepository
import com.kris.aurelvio.domain.usecase.AddAssetUseCase
import com.kris.aurelvio.domain.usecase.GetCategoriesUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AddAssetViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val categoryRepository = FakeCategoryRepository()
    private val assetRepository = FakeAssetRepository()
    private val entryRepository = FakeEntryRepository()

    private val getCategoriesUseCase = GetCategoriesUseCase(categoryRepository)
    private val addAssetUseCase = AddAssetUseCase(
        assetRepository = assetRepository,
        entryRepository = entryRepository,
        idGenerator = { "asset_test_1" },
    )

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_loadsCategoriesAndSelectsFirst() = runTest {
        val viewModel = AddAssetViewModel(
            getCategoriesUseCase = getCategoriesUseCase,
            addAssetUseCase = addAssetUseCase,
        )

        testScheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoadingCategories)
        assertEquals(4, state.categories.size)
        assertNotNull(state.selectedCategory)
        assertEquals(CategoryId("cat_accounts_deposits"), state.selectedCategory?.id)
    }

    @Test
    fun onAction_NameChanged_updatesState() = runTest {
        val viewModel = AddAssetViewModel(
            getCategoriesUseCase = getCategoriesUseCase,
            addAssetUseCase = addAssetUseCase,
        )
        testScheduler.advanceUntilIdle()

        viewModel.onAction(AddAssetAction.NameChanged("Mieszkanie"))

        assertEquals("Mieszkanie", viewModel.state.value.name)
        assertNull(viewModel.state.value.nameError)
    }

    @Test
    fun onAction_CategorySelected_updatesSelectedCategory() = runTest {
        val viewModel = AddAssetViewModel(
            getCategoriesUseCase = getCategoriesUseCase,
            addAssetUseCase = addAssetUseCase,
        )
        testScheduler.advanceUntilIdle()

        val newCategory = Category(
            id = CategoryId("cat_investments"),
            name = "Inwestycje",
            colorHex = "#7ED321",
        )
        viewModel.onAction(AddAssetAction.CategorySelected(newCategory))

        assertEquals(newCategory, viewModel.state.value.selectedCategory)
    }

    @Test
    fun onAction_ColorSelected_updatesSelectedColorHex() = runTest {
        val viewModel = AddAssetViewModel(
            getCategoriesUseCase = getCategoriesUseCase,
            addAssetUseCase = addAssetUseCase,
        )
        testScheduler.advanceUntilIdle()

        viewModel.onAction(AddAssetAction.ColorSelected("#D0021B"))

        assertEquals("#D0021B", viewModel.state.value.selectedColorHex)
    }

    @Test
    fun onAction_InitialValueChanged_sanitizesAndValidates() = runTest {
        val viewModel = AddAssetViewModel(
            getCategoriesUseCase = getCategoriesUseCase,
            addAssetUseCase = addAssetUseCase,
        )
        testScheduler.advanceUntilIdle()

        viewModel.onAction(AddAssetAction.InitialValueChanged("12 500 PLN"))

        assertEquals("12 500", viewModel.state.value.initialValueText)
        assertNull(viewModel.state.value.initialValueError)
    }

    @Test
    fun onAction_SaveClicked_withBlankName_setsNameError() = runTest {
        val viewModel = AddAssetViewModel(
            getCategoriesUseCase = getCategoriesUseCase,
            addAssetUseCase = addAssetUseCase,
        )
        testScheduler.advanceUntilIdle()

        viewModel.onAction(AddAssetAction.SaveClicked)

        assertEquals(AddAssetFormError.BLANK_NAME, viewModel.state.value.nameError)
        assertTrue(assetRepository.getAllAssets().isEmpty())
    }

    @Test
    fun onAction_SaveClicked_withValidForm_createsAssetAndEmitsEffects() = runTest {
        val viewModel = AddAssetViewModel(
            getCategoriesUseCase = getCategoriesUseCase,
            addAssetUseCase = addAssetUseCase,
        )
        testScheduler.advanceUntilIdle()

        viewModel.onAction(AddAssetAction.NameChanged("Konto walutowe EUR"))
        viewModel.onAction(AddAssetAction.InitialValueChanged("2500"))

        viewModel.effects.test {
            viewModel.onAction(AddAssetAction.SaveClicked)
            testScheduler.advanceUntilIdle()

            val effect1 = awaitItem()
            assertTrue(effect1 is AddAssetEffect.AssetCreated)
            assertEquals("Konto walutowe EUR", effect1.name)

            val effect2 = awaitItem()
            assertEquals(AddAssetEffect.NavigateBack, effect2)
        }

        val createdAssets = assetRepository.getAllAssets()
        assertEquals(1, createdAssets.size)
        assertEquals("Konto walutowe EUR", createdAssets.first().name)

        val createdEntries = entryRepository.getAllEntries()
        assertEquals(1, createdEntries.size)
        assertEquals(Money(2500L), createdEntries.first().value)
    }

    @Test
    fun onAction_BackClicked_emitsNavigateBackEffect() = runTest {
        val viewModel = AddAssetViewModel(
            getCategoriesUseCase = getCategoriesUseCase,
            addAssetUseCase = addAssetUseCase,
        )
        testScheduler.advanceUntilIdle()

        viewModel.effects.test {
            viewModel.onAction(AddAssetAction.BackClicked)
            assertEquals(AddAssetEffect.NavigateBack, awaitItem())
        }
    }
}
