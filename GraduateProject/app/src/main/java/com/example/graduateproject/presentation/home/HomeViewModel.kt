package com.example.graduateproject.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.graduateproject.domain.model.Product
import com.example.graduateproject.domain.usecase.GetCategoriesUseCase
import com.example.graduateproject.domain.usecase.GetHomeScreenProductsUseCase
import com.example.graduateproject.domain.usecase.SearchProductsUseCase
import com.example.graduateproject.presentation.utils.LanguageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val productsPagingFlow: Flow<PagingData<Product>> = _state
        .map { it.selectedCategory }
        .distinctUntilChanged()
        .flatMapLatest { category ->
            Pager(
                config = PagingConfig(
                    pageSize = 10, // Số sản phẩm hiển thị mỗi lần load
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    ProductPagingSource(getHomeScreenUseCase, category)
                }
            ).flow
        }
        .cachedIn(viewModelScope) // Cache lại dữ liệu khi xoay màn hình

    init {
        processIntent(HomeIntent.LoadInitialData)
    }

    fun processIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadInitialData -> loadInitialSetup()
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


    private fun loadInitialSetup() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val categoriesDeferred = async { getCategoriesUseCase() }

            val suggestionsDeferred = async { getHomeScreenUseCase(category = "All", page = 1, pageSize = 3) }

            val categoriesResult = categoriesDeferred.await()
            val suggestionsResult = suggestionsDeferred.await()

            // Update State
            _state.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    categories = categoriesResult.getOrDefault(emptyList()).toPersistentList(),
                    searchSuggestions = suggestionsResult.getOrDefault(emptyList()).toPersistentList()
                )
            }
        }
    }
    // vì products đã được Paging 3 tự động kích hoạt khi UI collect `productsPagingFlow`
    private fun loadCategories() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getCategoriesUseCase()
                .onSuccess { categories ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            categories = categories.toPersistentList()
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = "Lỗi tải danh mục") }
                    _effect.emit(HomeEffect.ShowError("Không thể tải danh mục trang chủ"))
                }
        }
    }

    // Chọn Category cực kỳ gọn! Chỉ update state, Paging 3 sẽ tự động làm nốt phần còn lại.
    private fun selectCategory(category: String) {
        if (_state.value.selectedCategory == category) return
        _state.update { it.copy(selectedCategory = category) }
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
}