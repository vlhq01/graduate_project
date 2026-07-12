package com.example.graduateproject.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduateproject.domain.usecase.GetCategoriesUseCase
import com.example.graduateproject.domain.usecase.GetHomeScreenProductsUseCase
import com.example.graduateproject.domain.usecase.SearchProductsUseCase
import com.example.graduateproject.presentation.utils.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeScreenUseCase: GetHomeScreenProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<HomeEffect>()
    val effect = _effect.asSharedFlow()

    private var searchJob: Job? = null

    init {
        processIntent(HomeIntent.LoadInitialData)
    }

    fun processIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadInitialData -> loadInitialData()
            is HomeIntent.SelectCategory -> selectCategory(intent.category)
            is HomeIntent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = intent.query) }
                executeSearch(intent.query)
            }

            is HomeIntent.ClearSearch -> {
                searchJob?.cancel()
                _state.update { it.copy(searchQuery = "", searchResults = persistentListOf()) }
            }

            is HomeIntent.ClickTrendingKeyword -> {
                _state.update { it.copy(searchQuery = intent.keyword) }
                executeSearch(intent.keyword)
            }

            is HomeIntent.SetSearchActive -> {
                _state.update { it.copy(isSearchActive = intent.isActive) }
            }

            is HomeIntent.ChangeLanguage -> {
                LanguageManager.setLanguage(intent.languageTag)
            }
        }
    }

    private fun executeSearch(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            _state.update { it.copy(searchResults = persistentListOf()) }
            return
        }

        searchJob = viewModelScope.launch {
            delay(500L)
            _state.update { it.copy(isSearching = true) }

            searchProductsUseCase(query)
                .onSuccess { results ->
                    _state.update {
                        it.copy(
                            isSearching = false,
                            searchResults = results.toPersistentList()
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isSearching = false) }
                    _effect.emit(HomeEffect.ShowError(error.message ?: "Lỗi tìm kiếm"))
                }
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val categoriesDeferred = async { getCategoriesUseCase() }
            val productsDeferred = async { getHomeScreenUseCase(_state.value.selectedCategory) }

            val categoriesResult = categoriesDeferred.await()
            val productsResult = productsDeferred.await()

            if (categoriesResult.isSuccess && productsResult.isSuccess) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        categories = categoriesResult.getOrDefault(emptyList()).toPersistentList(),
                        products = productsResult.getOrDefault(emptyList()).toPersistentList()
                    )
                }
            } else {
                // Xử lý lỗi
                _state.update { it.copy(isLoading = false, error = "Lỗi tải dữ liệu") }
                _effect.emit(HomeEffect.ShowError("Không thể tải dữ liệu trang chủ"))
            }
        }
    }

    private fun selectCategory(category: String) {
        if (_state.value.selectedCategory == category) return

        viewModelScope.launch {
            _state.update { it.copy(selectedCategory = category, isLoading = true, error = null) }

            getHomeScreenUseCase(category)
                .onSuccess { filteredProducts ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            products = filteredProducts.toPersistentList()
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.message) }
                }
        }
    }
}